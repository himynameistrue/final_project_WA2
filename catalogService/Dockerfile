FROM openjdk:12

ARG JAR_FILE="build/libs/catalog-0.0.1-SNAPSHOT.jar"
COPY ${JAR_FILE} catalog-service.jar

ENTRYPOINT [ "java", "-jar", "catalog-service.jar"]