echo "building order-service"
cd ./orderService && ./gradlew clean build

echo -e "\n\nbuilding orchestrator"
cd ../orchestrator && ./gradlew clean build

echo -e "\n\nbuilding wallet-service"
cd ../walletService && ./gradlew clean build

echo -e "\n\nbuilding warehouse-service"
cd ../warehouse-master && ./gradlew clean build

echo -e "\n\nbuilding debezium"
cd ../outboxTransformer && mvn package

docker-compose build