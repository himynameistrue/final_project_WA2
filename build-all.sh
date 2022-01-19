echo "building catalog-service"
cd ./catalogService && ./gradlew clean build

echo "building order-service"
cd ../orderService && ./gradlew clean build

echo -e "\n\nbuilding orchestrator"
cd ../orchestratorService && ./gradlew clean build

echo -e "\n\nbuilding wallet-service"
cd ../walletService && ./gradlew clean build

echo -e "\n\nbuilding warehouse-service"
cd ../warehouseService && ./gradlew clean build

echo -e "\n\nbuilding debezium"
cd ../debeziumConnectService && mvn package

docker-compose build