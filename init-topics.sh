./bin/kafka-configs.sh --alter \
      --add-config retention.ms=10000 \
      --bootstrap-server localhost:9092 \
      --topic "transaction-create"