package com.example.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.replica.PartitionView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class CustomPartitioner implements Partitioner {

    public static final Logger logger = LoggerFactory.getLogger(CustomPartitioner.class.getName());
    private String specialKeyName;
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitionInfoList = cluster.partitionsForTopic(topic);
        int numPartitions = partitionInfoList.size();
        int numSpecialPartitions = (int)(numPartitions * 0.5);

        if(((String)key).equals(specialKeyName)) {
             
        }

        // auto.commit.sync = true / false 

        int partitionIndex = 0;

        return partitionIndex;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {
        this.specialKeyName = configs.get("custom.specialKey").toString();
    }
}
