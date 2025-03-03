//package com.example.kafka;
//
//import com.Config;
//import org.apache.kafka.clients.producer.*;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Properties;
//import java.util.Random;
//import java.util.concurrent.ExecutionException;
//
//public class PizzaProducer {
//    public static final Logger logger = LoggerFactory.getLogger(PizzaProducer.class.getName());
//
//
//    public static void sendPizzaMessage(KafkaProducer<String, String> kafkaProducer,
//                                        String topicName, int iterCount,
//                                        int interIntervalMillis, int intervalMilli s,
//                                        int intervalCount, boolean sync) {
//
//        PizzaMessage pizzaMessage = new PizzaMessage();
//        int iterSeq = 0;
//        long seed = 2022;
//        Random random = new Random(seed);
//        Faker faker = Faker.instance(random);
//
//        long startTime = System.currentTimeMillis();
//
//        while( iterSeq++ != iterCount ) {
//            HashMap<String, String> pMessage = pizzaMessage.produce_msg(faker, random, iterSeq);
//            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName,
//                    pMessage.get("key"), pMessage.get("message"));
//            sendMessage(kafkaProducer, producerRecord, pMessage, sync);
//
//            if((intervalCount > 0) && (iterSeq % intervalCount == 0)) {
//                try {
//                    logger.info("####### IntervalCount:" + intervalCount +
//                            " intervalMillis:" + intervalMillis + " #########");
//                    Thread.sleep(intervalMillis);
//                } catch (InterruptedException e) {
//                    logger.error(e.getMessage());
//                }
//            }
//
//            if(interIntervalMillis > 0) {
//                try {
//                    logger.info("interIntervalMillis:" + interIntervalMillis);
//                    Thread.sleep(interIntervalMillis);
//                } catch (InterruptedException e) {
//                    logger.error(e.getMessage());
//                }
//            }
//
//        }
//        long endTime = System.currentTimeMillis();
//        long timeElapsed = endTime - startTime;
//
//        logger.info("{} millisecond elapsed for {} iterations", timeElapsed, iterCount);
//
//    }
//
//    public static void sendMessage(KafkaProducer<String, String> kafkaProducer,
//                                   ProducerRecord<String, String> producerRecord,
//                                   HashMap<String, String> pMessage, boolean sync) {
//        if(!sync) {
//            kafkaProducer.send(producerRecord, (metadata, exception) -> {
//                if (exception == null) {
//                    logger.info("async message:" + pMessage.get("key") + " partition:" + metadata.partition() +
//                            " offset:" + metadata.offset());
//                } else {
//                    logger.error("exception error from broker " + exception.getMessage());
//                }
//            });
//        } else {
//            try {
//                RecordMetadata metadata = kafkaProducer.send(producerRecord).get();
//                logger.info("sync message:" + pMessage.get("key") + " partition:" + metadata.partition() +
//                        " offset:" + metadata.offset());
//            } catch (ExecutionException e) {
//                logger.error(e.getMessage());
//            } catch (InterruptedException e) {
//                logger.error(e.getMessage());
//            }
//        }
//
//    }
//
//    public static void main(String[] args) throws IOException {
//
//        String topicName = "pizza-topic";
//        Config config = new Config();
//        String serverIp = config.getServerIp();
//
//        //KafkaProducer configuration setting
//        // null, "hello world"
//
//        Properties props  = new Properties();
//        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverIp);
//        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        //props.setProperty(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "50000");
//        //props.setProperty(ProducerConfig.ACKS_CONFIG, "0");
//
//
//        //KafkaProducer object creation
//        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
//
//        sendPizzaMessage(kafkaProducer, topicName,
//                -1, 1000, 0, 0, false);
//
//        kafkaProducer.close();
//
//    }
//}