# Loki Log Aggregation Setup

This project now includes Loki for log aggregation alongside Prometheus and Grafana.

## Components Added

### 1. Loki
- **Port**: 3100
- **Purpose**: Log aggregation system
- **Config**: `loki-config-simple.yml` (simplified for better compatibility)
- **Version**: 2.8.7 (stable version)

### 2. Promtail
- **Purpose**: Log collector that ships logs to Loki
- **Config**: `promtail-config.yml`
- **Collects**: Docker/Podman container logs from Spring Boot application

### 3. Enhanced Logging
- **JSON Format**: Structured logging using logstash-logback-encoder
- **Configuration**: `logback-spring.xml`
- **Log Levels**: Configured in `application.properties`

## Getting Started

### For Docker:
1. **Build and start all services**:
   ```bash
   mvn clean package
   docker-compose up --build
   ```

### For Podman:
1. **Build and start all services**:
   ```bash
   mvn clean package
   podman-compose up --build
   ```
   
   Or if using podman-compose is not available:
   ```bash
   podman play kube docker-compose.yml
   ```

2. **Access the services**:
   - Spring Boot App: http://localhost:8888
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)
   - Loki: http://localhost:3100

3. **Configure Grafana for Loki**:
   - Go to Configuration → Data Sources
   - Add Loki data source
   - URL: `http://loki:3100`
   - Save & Test

4. **View Logs in Grafana**:
   - Go to Explore
   - Select Loki data source
   - Use LogQL queries like: `{job="spring-boot-app"}`

## LogQL Query Examples

- All logs from Spring Boot app: `{job="spring-boot-app"}`
- Error logs only: `{job="spring-boot-app"} |= "ERROR"`
- Logs from specific container: `{container="spring-boot-application"}`
- Time-based filtering: `{job="spring-boot-app"}[5m]`
- Filter by log level: `{job="spring-boot-app"} | json | level="ERROR"`

## Log Structure

The application now outputs structured JSON logs with:
- timestamp
- log level
- logger name
- message
- stack trace (for errors)
- MDC context (if any)

## Troubleshooting

### Schema Configuration Issues
If you encounter schema v13/v11 errors:
- The configuration now uses schema v9 with boltdb for better compatibility
- Structured metadata is disabled to avoid compatibility issues
- Using Loki 2.8.7 for stability

### Podman Specific Issues
- Ensure Podman is running: `podman system info`
- For SELinux issues, add `:Z` to volume mounts if needed
- Check container logs: `podman logs [container-name]`

### General Troubleshooting
- Check Loki status: `curl http://localhost:3100/ready`
- Check Promtail targets: `curl http://localhost:9080/targets`
- View container logs: `podman logs [service-name]` or `docker-compose logs [service-name]`

## Configuration Files

- `loki-config-simple.yml`: Simplified Loki configuration for better compatibility
- `loki-config.yml`: Advanced configuration (backup, may have compatibility issues)
- `promtail-config.yml`: Promtail configuration for log collection
