# # Starting from Redhat UBI 8 Micro Image
# FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest
# LABEL author="sandeep.rana"

# COPY target/*.jar executable.jar

# ENTRYPOINT ["java","-jar","-noverify","executable.jar"]

FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest
LABEL author="sandeep.rana"

ARG OTEL_AGENT_VERSION=2.16.0

# Download to a writable directory (e.g., /opt)
RUN curl --silent --fail --insecure -L \
    "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    -o /opt/opentelemetry-javaagent.jar

COPY target/*.jar /executable.jar

ENTRYPOINT ["java", \
  "-javaagent:/opt/opentelemetry-javaagent.jar", \
  "-jar", "-noverify", "/executable.jar"]
