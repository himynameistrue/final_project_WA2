package com.group1.outbox;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.header.Headers;
import org.apache.kafka.connect.transforms.Transformation;

import java.util.Map;

/**
 * The class is configured and invoked when a changes occur on any OutBox Schema.
 *
 * @author Sohan
 */
public class CustomTransformation<R extends ConnectRecord<R>> implements Transformation<R> {

    /**
     * This method is invoked when a change is made on the outbox schema.
     *
     * @param sourceRecord
     * @return
     */
    public R apply(R sourceRecord) {

        Struct kStruct = (Struct) sourceRecord.value();
        String databaseOperation = kStruct.getString("op");

        //Handle only the Create's
        if ("c".equalsIgnoreCase(databaseOperation)) {

            // Get the details.
            Struct after = (Struct) kStruct.get("after");

            byte[] correlationId = after.getBytes("correlationId");
            byte[] payload = after.getBytes("payload");
            String eventType = after.getString("replyTopic");
            String topic = eventType.toLowerCase();

            Headers headers = sourceRecord.headers();
            //headers.addString("eventId", UUID);

            // Build the event to be published.
            sourceRecord = sourceRecord.newRecord(topic, null, Schema.STRING_SCHEMA, eventType,
                    null, payload, sourceRecord.timestamp(), headers);
        }

        return sourceRecord;
    }

    public ConfigDef config() {
        return new ConfigDef();
    }

    public void close() {

    }

    public void configure(Map<String, ?> configs) {

    }
}