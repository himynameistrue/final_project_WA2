curl -X POST \
  http://localhost:8083/connectors/ \
  -H 'content-type: application/json' \
  -d '{
   "name": "warehouse-outbox-connector",
   "config": {
      "connector.class": "io.debezium.connector.mysql.MySqlConnector",
      "tasks.max": "1",
      "database.hostname": "mysql",
      "database.port": "3306",
      "database.user": "root",
      "database.password": "debezium",
      "database.include.list": "warehouse_service_db",
      "database.server.id": "184054",
      "database.server.name": "dbserver1",
      "database.history.kafka.bootstrap.servers": "kafka:9092",
      "database.history.kafka.topic": "dbhistory.fullfillment",
      "tombstones.on.delete": "false",
      "table.whitelist": "warehouse_outbox",
      "table.field.event.key": "id",
      "table.field.event.payload": "payload",
      "route.by.field": "replyTopic",
      "transforms": "outbox",
      "transforms.outbox.type": "io.debezium.transforms.outbox.EventRouter"
   }
}'


curl -i -X POST '{  "name": "inventory-connector",  "config": { "database.server.id": "184054",    "database.history.kafka.bootstrap.servers": "kafka:9092",    "database.history.kafka.topic": "schema-changes.inventory",    "provide.transaction.metadata": true  }}'