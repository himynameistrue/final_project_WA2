./bin/kafka-configs.sh --alter \
      --add-config retention.ms=10000 \
      --bootstrap-server localhost:9092 \
      --topic "order-create-orchestrator-to-wallet"