FROM openjdk:12

ARG JAR_FILE="build/libs/orchestrator-0.0.1-SNAPSHOT.jar"
COPY ${JAR_FILE} orchestrator-service.jar

ENTRYPOINT [ "java", "-jar", "orchestrator-service.jar"]