package com.example.kafka;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
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
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.*;

public class SourceConnectorAPI {
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
        String serverIp = "localhost:9092"; // Kafka 브로커 주소

        // Kafka Producer 설정
        Properties props = new Properties();
        props.put("bootstrap.servers", serverIp);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class); // JSON 문자열로 전송

        // Kafka Producer 생성
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 전송할 데이터
        String topic = "dbz_test";

        // Avro 스키마 정의
        String schemaString = "{\n" +
                "    \"type\": \"record\",\n" +
                "    \"name\": \"DbzTest\",\n" +
                "    \"fields\": [\n" +
                "        {\"name\": \"id\", \"type\": \"string\"},\n" +
                "        {\"name\": \"field1\", \"type\": \"string\"},\n" +
                "        {\"name\": \"field2\", \"type\": \"string\"}\n" +
                "    ]\n" +
                "}";
        Schema schema = new Schema.Parser().parse(schemaString);

        // Avro 레코드 생성
        GenericRecord record = new GenericData.Record(schema);
        record.put("id", "10001");
        record.put("field1", "TEST Field1");
        record.put("field2", "TEST Field2");

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
                .put("id", record.get("id"))
                .put("field1", record.get("field1"))
                .put("field2", record.get("field2"))
        );

        // ProducerRecord 생성 및 전송
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, "1", jsonPayload.toString());
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
//    public static void main(String[] args) throws IOException {
//        Config config = new Config();
//        String serverIp = config.getServerIp();
//
//        // Kafka Producer 설정
//        Properties props = new Properties();
//        props.put("bootstrap.servers", serverIp); // Kafka 브로커 주소
//        props.put("key.serializer", StringSerializer.class);
////        props.put("value.serializer", JsonSerializer.class);
//        props.put("value.serializer", StringSerializer.class);
//
//        // Kafka Producer 생성
//        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
//
//        // 전송할 데이터
//        String topic = "dbz_test";
//
//        // JSON 형식의 데이터 (PostgreSQL에 적합한 형식으로)
//        String key = "1"; // 고유 키
//        String value = "{\n" +
//                "    \"schema\": {\n" +
//                "        \"type\": \"struct\",\n" +
//                "        \"name\": \"dbz_test\",\n" +
//                "        \"optional\": false,\n" +
//                "        \"fields\": [{\n" +
//                "                \"field\": \"id\",\n" +
//                "                \"optional\": false,\n" +
//                "                \"type\": \"string\"\n" +
//                "            }, {\n" +
//                "                \"field\": \"field1\",\n" +
//                "                \"optional\": false,\n" +
//                "                \"type\": \"string\"\n" +
//                "            }, {\n" +
//                "                \"field\": \"field2\",\n" +
//                "                \"optional\": false,\n" +
//                "                \"type\": \"string\"\n" +
//                "            }\n" +
//                "        ]\n" +
//                "    },\n" +
//                "    \"payload\": {\n" +
//                "        \"id\": \"10001\",\n" +
//                "        \"field1\": \"TEST Field1\",\n" +
//                "        \"field2\": \"TEST Field2\"\n" +
//                "    }\n" +
//                "}";
//
//        JSONObject jsonObject = new JSONObject(value);
//        // ProducerRecord 생성
//        // JSON 객체 생성
////        TestJsonClass jsonData = new TestJsonClass();
////        jsonData.setId(1);
////        jsonData.setField1("test");
////        jsonData.setField2("testvalue");
//
//        // ProducerRecord 생성 및 전송
//        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "1", jsonObject.toString());
////        ProducerRecord<String, TestJsonClass> record = new ProducerRecord<>(topic, key, jsonData);
//        //ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
//
//        // 데이터 전송
//        producer.send(record, (RecordMetadata metadata, Exception e) -> {
//            if (e != null) {
//                e.printStackTrace();
//                System.out.println("exeception : " + e.getMessage());
//            } else {
//                System.out.println("Sent record: " + metadata);
//            }
//        });
//
//        // Producer 종료
//        producer.close();
//    }


//        String value = "{\n" +
//                "  \"key\": {\n" +
//                "    \"type\": \"struct\",\n" +
//                "    \"fields\": [\n" +
//                "      {\n" +
//                "        \"type\": \"int32\",\n" +
//                "        \"field\": \"id\"\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"optional\": false\n" +
//                "  },\n" +
//                "  \"value\": {\n" +
//                "    \"type\": \"struct\",\n" +
//                "    \"fields\": [\n" +
//                "      {\n" +
//                "        \"type\": \"struct\",\n" +
//                "        \"field\": \"before\",\n" +
//                "        \"fields\": [\n" +
//                "          {\n" +
//                "            \"type\": \"int32\",\n" +
//                "            \"field\": \"id\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"field1\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"field2\"\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"optional\": true\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"type\": \"struct\",\n" +
//                "        \"field\": \"after\",\n" +
//                "        \"fields\": [\n" +
//                "          {\n" +
//                "            \"type\": \"int32\",\n" +
//                "            \"field\": \"id\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"field1\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"field2\"\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"optional\": false\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"type\": \"struct\",\n" +
//                "        \"field\": \"source\",\n" +
//                "        \"fields\": [\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"version\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"connector\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"name\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"schema\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"string\",\n" +
//                "            \"field\": \"table\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"int64\",\n" +
//                "            \"field\": \"txId\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"int64\",\n" +
//                "            \"field\": \"lsn\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"boolean\",\n" +
//                "            \"field\": \"snapshot\"\n" +
//                "          },\n" +
//                "          {\n" +
//                "            \"type\": \"int64\",\n" +
//                "            \"field\": \"time\"\n" +
//                "          }\n" +
//                "        ],\n" +
//                "        \"optional\": false\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"type\": \"string\",\n" +
//                "        \"field\": \"op\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"type\": \"int64\",\n" +
//                "        \"field\": \"ts_ms\"\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"optional\": false\n" +
//                "  }\n" +
//                "}\n"; // JSON 형식의 데이터

