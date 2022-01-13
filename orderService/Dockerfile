FROM openjdk:12

ARG JAR_FILE="build/libs/order-0.0.1-SNAPSHOT.jar"
COPY ${JAR_FILE} order-service.jar

ENTRYPOINT [ "java", "-jar", "order-service.jar"]