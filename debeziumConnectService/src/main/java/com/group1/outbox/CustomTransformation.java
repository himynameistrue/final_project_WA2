package com.group1.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.transforms.Transformation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.nio.charset.StandardCharsets;

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
        System.out.println(kStruct);

        String databaseOperation = kStruct.getString("op");

        // Only handle create
        if ("c".equalsIgnoreCase(databaseOperation)) {

            // Get the details.
            Struct after = (Struct) kStruct.get("after");

            String correlationId = after.getString("correlation_id");
            String payloadType = after.getString("payload_type");
            String payload = after.getString("payload");
            String replyTopic = after.getString("reply_topic");

            System.out.println("correlationId:");
            System.out.println(correlationId);
            System.out.println(Arrays.toString(correlationId.getBytes(StandardCharsets.UTF_8)));


            System.out.println("payload:");
            System.out.println(payload);

            ObjectMapper mapper = new ObjectMapper();
            try {
                Map payloadObject = mapper.readValue(payload, Map.class);

                ConnectHeaders headers = new ConnectHeaders();
                headers.addString("__TypeId__", payloadType);
                headers.addString("kafka_correlationId", correlationId);

                System.out.println("headers:");
                System.out.println(headers.toString());

                // Build the event to be published.
                sourceRecord = sourceRecord.newRecord(replyTopic, // topic
                        null, // partition
                        null, // key schema
                        null, // key
                        null, // value schema
                        payloadObject, // value
                        sourceRecord.timestamp(), // timestamp
                        headers); //header
            } catch (IOException e) {
                e.printStackTrace();
            }
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