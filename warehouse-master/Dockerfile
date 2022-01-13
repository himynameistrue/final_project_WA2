FROM openjdk:12

ARG JAR_FILE="build/libs/warehouse-0.0.1-SNAPSHOT.jar"
COPY ${JAR_FILE} warehouse-service.jar

ENTRYPOINT [ "java", "-jar", "warehouse-service.jar"]