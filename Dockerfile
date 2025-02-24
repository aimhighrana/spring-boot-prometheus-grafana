# Starting from Redhat UBI 8 Micro Image
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest
LABEL author="sandeep.rana"

COPY target/*.jar executable.jar

ENTRYPOINT ["java","-jar","-noverify","executable.jar"]