# Connector registration options
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
      "table.include.list": "warehouse_service_db.warehouse_outbox",
      "database.server.id": "184054",
      "database.server.name": "dbserver1",
      "database.history.kafka.bootstrap.servers": "kafka:29092",
      "database.history.kafka.topic": "dbhistory.fullfillment",
      "tombstones.on.delete": "false",
      "transforms": "outbox",
      "transforms.outbox.type": "com.group1.outbox.CustomTransformation",
      "include.schema.changes":"false",
      "errors.log.enable": "true",
      "errors.log.include.messages": "true",
      "value.converter": "org.apache.kafka.connect.json.JsonConverter",
      "value.converter.schemas.enable": "false"
   }
}'