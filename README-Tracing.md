# Distributed Tracing with Tempo, OpenTelemetry, and Grafana

This guide explains how to use the distributed tracing capabilities added to the Spring Boot application.

## Overview

The following components have been added to your observability stack:

1. **Tempo** - Grafana's distributed tracing backend
2. **OpenTelemetry** - For instrumenting the Java application
3. **Micrometer Tracing** - For integrating with Spring Boot

## Architecture

The tracing architecture follows this flow:

1. Spring Boot application generates traces using OpenTelemetry
2. Traces are sent to Tempo via OTLP (OpenTelemetry Protocol)
3. Grafana visualizes traces and connects them with logs from Loki and metrics from Prometheus

## How to View Traces

1. Start the Docker Compose environment:
   ```
   docker-compose up -d
   ```

2. Access the tracing demo endpoint to generate some traces:
   ```
   curl http://localhost:8888/api/trace-demo
   ```

3. Open Grafana at http://localhost:3000 (login with admin/admin)

4. Navigate to Explore (left sidebar)

5. Select Tempo as the data source (top left dropdown)

6. You can search for traces by:
   - Service name: `spring-boot-prometheus-grafana`
   - Operation names: `/api/trace-demo`, `/api/service-a`, etc.
   - Duration
   - Tags

7. Click on a trace to view its details, including:
   - Timeline of spans
   - Span details (tags, logs, etc.)
   - Service graph

## Features

### Trace-to-Logs Correlation

When viewing a trace, you can click on a span and then click "Logs for this span" to view the logs associated with that span in Loki.

### Trace-to-Metrics Correlation

When viewing a trace, you can click on a span and then click "Metrics for this span" to view the metrics associated with that span in Prometheus.

### Service Graph

Tempo generates service graphs that show the relationships between services and operations. You can view these in Grafana.

## Sample Endpoints

The application includes several endpoints to demonstrate distributed tracing:

- `/api/trace-demo` - Orchestrates multiple nested API calls
- `/api/service-a` - A sample service endpoint that makes a nested call
- `/api/service-b` - Another sample service endpoint that makes a nested call
- `/api/nested/{parent}` - A nested service called by both service A and B

Each request generates spans that are captured in the trace, showing the full request flow including timing information.

## Additional Information

- Trace sampling rate is set to 100% (1.0) for demonstration purposes. In production, you might want to lower this.
- Trace data is stored locally in the Tempo container. For production, consider using a persistent storage option.
- Logs include trace and span IDs for correlation with Loki.
