package com.example.kafka;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.connect.source.SourceRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.Config;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void main(String[] args) {
        // Kafka Producer 설정
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092"); // Kafka 브로커 주소
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Kafka Producer 생성
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 전송할 데이터
        String topic = "test";
        String key = "exampleKey";
        String value = "{\"field1\":\"value1\", \"field2\":\"value2\"}"; // JSON 형식의 데이터

        // ProducerRecord 생성
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

        // 데이터 전송
        producer.send(record, (RecordMetadata metadata, Exception e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                System.out.println("Sent record: " + metadata);
            }
        });

        // Producer 종료
        producer.close();
    }
}
