# RabbitMQ Integration Guide

This document describes the RabbitMQ messaging integration added to the Spring Boot Prometheus Grafana application.

## Overview

The application now includes RabbitMQ 3.14 with management console for asynchronous message processing. The implementation includes:
- 2 separate queues with dedicated consumers
- Fire-and-forget message consumption pattern
- REST API endpoints for testing message publishing
- Full observability integration with existing Prometheus/Grafana stack

## Architecture

### Queues and Exchanges

- **Exchange**: `demo.exchange` (Topic Exchange)
- **Queue 1**: `first.queue` with routing key `first.routing.key`
- **Queue 2**: `second.queue` with routing key `second.routing.key`

### Components

1. **RabbitMQConfig** - Configures queues, exchanges, and bindings
2. **FirstMessageConsumer** - Listens to `first.queue` and logs messages
3. **SecondMessageConsumer** - Listens to `second.queue` and logs messages
4. **MessageProducerService** - Service for sending messages to queues
5. **RabbitMQController** - REST API endpoints for testing

## Getting Started

### 1. Start the Services

```bash
# Build and start all services including RabbitMQ
docker-compose up --build -d

# Or if using Podman
podman-compose up --build -d
```

### 2. Access RabbitMQ Management Console

- **URL**: http://localhost:15672
- **Username**: guest
- **Password**: guest

From the management console, you can:
- View queues and their message counts
- Monitor message rates
- View connections and channels
- Manage exchanges and bindings

### 3. Access the Spring Boot Application

- **Application URL**: http://localhost:8888
- **Health Check**: http://localhost:8888/actuator/health

## Testing the RabbitMQ Integration

### Using REST API Endpoints

The application provides three REST endpoints for testing:

#### 1. Send Message to First Queue

```bash
curl -X POST "http://localhost:8888/api/rabbitmq/send/first?message=Hello%20First%20Queue"
```

Response:
```json
{
  "status": "success",
  "message": "Message sent to first queue",
  "payload": "Hello First Queue"
}
```

#### 2. Send Message to Second Queue

```bash
curl -X POST "http://localhost:8888/api/rabbitmq/send/second?message=Hello%20Second%20Queue"
```

Response:
```json
{
  "status": "success",
  "message": "Message sent to second queue",
  "payload": "Hello Second Queue"
}
```

#### 3. Send Message to Both Queues

```bash
curl -X POST "http://localhost:8888/api/rabbitmq/send/both?message=Hello%20Both%20Queues"
```

Response:
```json
{
  "status": "success",
  "message": "Message sent to both queues",
  "payload": "Hello Both Queues"
}
```

### Verify Message Consumption

Check the application logs to see the consumers processing messages:

```bash
# View logs
docker logs -f spring-boot-application

# Or with Podman
podman logs -f spring-boot-application
```

Expected log output:
```
📤 Sending message to first queue: Hello First Queue
✅ Message sent to first queue successfully
✅ [First Consumer] Received message: Hello First Queue
```

## Configuration

### Local Development (application.yaml)

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### Docker Environment (docker-compose.yml)

The Spring Boot application container uses these environment variables:
```yaml
- SPRING_RABBITMQ_HOST=rabbitmq
- SPRING_RABBITMQ_PORT=5672
- SPRING_RABBITMQ_USERNAME=guest
- SPRING_RABBITMQ_PASSWORD=guest
```

### Custom Queue Configuration

To add or modify queues, update `application.yaml`:

```yaml
rabbitmq:
  queue:
    first: first.queue
    second: second.queue
  exchange: demo.exchange
  routing-key:
    first: first.routing.key
    second: second.routing.key
```

## Consumer Pattern: Fire-and-Forget

Both consumers use a **fire-and-forget** pattern:
- Messages are consumed from the queue
- Logged to the application logs
- Automatically acknowledged (no retry or DLQ)
- No response is sent back

This pattern is ideal for:
- Event logging
- Audit trails
- Fire-and-forget notifications
- Non-critical async operations

## Monitoring

### Prometheus Metrics

The application exposes custom RabbitMQ consumer metrics to Prometheus at `/actuator/prometheus`:

#### Available Metrics

1. **rabbitmq_consumer_processing_duration_seconds**
   - Type: Timer/Histogram
   - Description: Time taken to process RabbitMQ messages
   - Tags: `queue`, `tenant`, `status`
   - Percentiles: p50, p90, p95, p99

2. **rabbitmq_consumer_messages_processed_total**
   - Type: Counter
   - Description: Total number of messages processed
   - Tags: `queue`, `tenant`, `status`

3. **rabbitmq_consumer_errors_total**
   - Type: Counter
   - Description: Total number of processing errors
   - Tags: `queue`, `tenant`, `error`

#### Example Prometheus Queries

```promql
# 95th percentile processing time by tenant and queue
histogram_quantile(0.95, sum(rate(rabbitmq_consumer_processing_duration_seconds_bucket[5m])) by (tenant, queue, le))

# Message throughput by tenant
sum(rate(rabbitmq_consumer_messages_processed_total[5m])) by (tenant, queue, status)

# Average processing time in milliseconds
avg(rabbitmq_consumer_processing_duration_seconds_sum / rabbitmq_consumer_processing_duration_seconds_count) by (tenant, queue) * 1000

# Total errors by tenant
sum(rabbitmq_consumer_errors_total) by (queue, tenant)
```

### Grafana Dashboard

A pre-configured dashboard is available at:
- **Dashboard Name**: RabbitMQ Consumer Metrics by Tenant
- **Location**: `grafana-provisioning/dashboards/rabbitmq-consumer-metrics.json`
- **Access**: http://localhost:3000/d/rabbitmq-consumer-metrics

The dashboard includes:
1. **Processing Time p95** - Shows 95th percentile processing time by tenant and queue
2. **Message Throughput** - Message processing rate by tenant, queue, and status
3. **Average Processing Time** - Bar gauge showing average time by tenant
4. **Total Messages Processed** - Message count by queue
5. **Message Distribution by Tenant** - Pie chart showing tenant message distribution
6. **Total Errors** - Error count by queue and tenant
7. **Processing Time Percentiles** - Comprehensive view of p50, p90, p99 by tenant

#### Using the Dashboard

1. Start your services: `docker-compose up -d`
2. Send test messages with tenant headers
3. Open Grafana: http://localhost:3000
4. Navigate to Dashboards → RabbitMQ Consumer Metrics by Tenant
5. Observe metrics in real-time (auto-refresh every 5 seconds)

**Example: Identify Slow Tenants**
- Look at "Average Processing Time by Tenant" panel
- Tenants like `72838` (1-3s delay) and `38432` (4-6s delay) will show higher processing times
- Compare across different queues (cr_queue vs bulk_queue)

### RabbitMQ Management Console

Access RabbitMQ metrics through the management console:
- Message rates (publish/deliver/ack)
- Queue depth
- Consumer count
- Connection statistics

### Application Logs

Consumer activity is logged and can be viewed in:
- Local logs: `./logs/` directory
- Grafana Loki: http://localhost:3000 (query for "Consumer")
- Container logs: `docker logs spring-boot-application`

## Troubleshooting

### RabbitMQ Not Starting

Check RabbitMQ logs:
```bash
docker logs rabbitmq
# or
podman logs rabbitmq
```

Verify RabbitMQ is healthy:
```bash
docker exec rabbitmq rabbitmq-diagnostics ping
```

### Application Can't Connect to RabbitMQ

1. Ensure RabbitMQ container is running:
   ```bash
   docker ps | grep rabbitmq
   ```

2. Check network connectivity:
   ```bash
   docker exec spring-boot-application ping rabbitmq
   ```

3. Verify RabbitMQ is accepting connections:
   ```bash
   docker exec rabbitmq rabbitmq-diagnostics listeners
   ```

### Messages Not Being Consumed

1. Check if consumers are registered:
   - Open RabbitMQ Management Console
   - Navigate to Queues tab
   - Verify consumers are connected to each queue

2. Check application logs for errors:
   ```bash
   docker logs spring-boot-application | grep -i error
   ```

3. Verify queues are bound correctly:
   - RabbitMQ Management Console → Exchanges → demo.exchange → Bindings

## Production Considerations

For production deployments, consider:

1. **Security**
   - Use strong passwords (not default guest/guest)
   - Enable TLS/SSL for connections
   - Restrict management console access

2. **High Availability**
   - Use RabbitMQ clustering
   - Configure queue mirroring
   - Set up load balancers

3. **Monitoring**
   - Enable RabbitMQ Prometheus plugin
   - Set up alerts for queue depth
   - Monitor consumer lag

4. **Error Handling**
   - Implement Dead Letter Exchanges (DLQ)
   - Add retry mechanisms
   - Configure message TTL

5. **Performance**
   - Tune consumer concurrency
   - Configure prefetch counts
   - Use appropriate acknowledgment modes

## References

- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [Spring AMQP Reference](https://docs.spring.io/spring-amqp/reference/)
- [RabbitMQ Management Plugin](https://www.rabbitmq.com/management.html)
