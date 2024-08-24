package com.example.kafka;

import com.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SimpleProducerSync {
    public static void main(String[] args) throws IOException {
        Config config = new Config();
        String serverIp = config.getServerIp();

        String topicName = "simple-topic";
        //KafkaProducer configuration setting
        // key:null, value:"hello world"
        Properties props = new Properties();
        //bootstrap.servers, key.serializer.class, value.serializer.class
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverIp;
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //KafkaProducer Object create
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);

        //ProducerRecord Object create
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, "hello world2");

        //KafkaProducer message send sync방식
        Future<RecordMetadata> future = kafkaProducer.send(producerRecord);

        try {
            RecordMetadata recordMetadata = kafkaProducer.send(producerRecord).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            kafkaProducer.close();
        }

        kafkaProducer.flush(); // 배치로 수행되기 때문에 바로 메세지가 가지 않음 그런 처리. 버퍼에 있던 거 비우기
        kafkaProducer.close();



    }
}
