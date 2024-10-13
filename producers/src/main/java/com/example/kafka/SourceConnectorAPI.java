package com.example.kafka;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.source.SourceRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.Config;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SourceConnectorAPI { // Oracle -> Oracle Source Connector -> Kafka Topic -> Postgres Sink Connector -> Postgres
    public static final Logger logger = LoggerFactory.getLogger(SourceConnectorAPI.class.getName());

    public class MySourceTask extends SourceTask {

        Config config = new Config();
        String serverIp = config.getServerIp();

        public MySourceTask() throws IOException {   
        }

        @Override
        public String version() {
            return null;
        }

        @Override
        public void start(Map<String, String> props) {

        }

        @Override
        public List<SourceRecord> poll() {
            // 데이터를 생성
            List<SourceRecord> records = new ArrayList<>();

            // 예시 데이터
            Map<String, String> value = new HashMap<>();
            value.put("key", "value");

            // SourceRecord 생성
            SourceRecord record = new SourceRecord(
                    Collections.singletonMap("source_partition", "1"),
                    Collections.singletonMap("source_offset", "1"),
                    "your-topic-name", // Kafka 토픽 이름
                    null, // 키 (null일 경우 자동 생성)
                    null, // 값
                    value // 데이터
            );

            records.add(record);
            return records;
        }

        @Override
        public void stop() {

        }
    }

    public static void main(String[] args) throws IOException {
        Config config = new Config();
        String serverIp = config.getServerIp();

        // Kafka Producer 설정
        Properties props = new Properties();
        props.put("bootstrap.servers", serverIp);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class); // JSON 문자열로 전송

        // Kafka Producer 생성
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 전송할 데이터
        String topic = "dbz_test";

        // JSON 포맷으로 변환
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("schema", new JSONObject()
                .put("type", "struct")
                .put("name", "dbz_test")
                .put("optional", false)
                .put("fields", new JSONArray()
                        .put(new JSONObject().put("field", "id").put("optional", false).put("type", "string"))
                        .put(new JSONObject().put("field", "field1").put("optional", false).put("type", "string"))
                        .put(new JSONObject().put("field", "field2").put("optional", false).put("type", "string"))
                ));
        jsonPayload.put("payload", new JSONObject()
                .put("id", "00001")
                .put("field1", "TEST Field1")
                .put("field2", "TEST Field2")
        );

        System.out.println(jsonPayload.toString());
        // ProducerRecord 생성 및 전송
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic,  jsonPayload.toString());
        producer.send(producerRecord);

        // Producer 종료
        producer.close();
    }

    private static byte[] serializeAvroRecord(Schema schema, GenericRecord record) throws IOException {
        DatumWriter<GenericRecord> datumWriter = new SpecificDatumWriter<>(schema);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        datumWriter.write(record, encoder);
        encoder.flush();
        return out.toByteArray();             
    }
}