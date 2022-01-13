FROM openjdk:12

ARG JAR_FILE="build/libs/wallet-0.0.1-SNAPSHOT.jar"
COPY ${JAR_FILE} wallet-service.jar

ENTRYPOINT [ "java", "-jar", "wallet-service.jar"]