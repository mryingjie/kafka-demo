package com.demo.kafka.simple;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

/**
 * @author ZhengYingjie
 * @time 2019/3/7 9:29
 * @description 简单的Kafka producer代码
 * 包含两个功能
 * 1.数据发送
 * 2.数据按照自定义的partition策略发送
 */
public class KafkaProducerSimple {

    public static void main(String[] args) throws InterruptedException {
        /**
         * 1.指定当前kafka producer生产者发送数据的目的地
         * 创建topic的命令：
         * ./kafka-topics.sh --create --topic order --replication-factor 2 --partitions 4 --zookeeper hadoop1002:2181
         */
        String topic = "order";

//        String BROKER_LIST = "192.168.42.132:9092";
        String BROKER_LIST = "hadoop.abc6.net:9092";
//        String BROKER_LIST = "hadoop.abc6.net:9092,zhengyingjie2.abc6.net:9092,zhengyingjie3.abc6.net:9092";

        /**
         * 2.读取配置文件
         */
        Properties props = new Properties();

        /**
         * key.serializer.class 默认为Serializable.class
         */
//        props.put("serializer.class", "kafka.serializer.StringDecoder");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());


        /**
         * kafak broker对应的主机
         */
//        props.put("metadate.broker.list", "hadoop.abc6.net:9092,zhengyingjie2.abc6.net:9092,zhengyingjie3.abc6.net:9092");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);

        /**
         * request.required.acks 设置发送数据是否需要服务器的反馈 三个值0 1 -1
         * 0 表示producer永远不会等待一个来自broker的ack
         * 1 表示在leader replica收到数据后 就会返回ack
         * 但是如果刚写到leader 还没写到replica上就挂掉了 数据可能丢失
         * -1 表示所有的副本都收到数据了才会返回ack
         *
         * 默认
         */
        props.put(ProducerConfig.ACKS_CONFIG, "1");


        /**
         * 分区配置 默认是org.apache.kafka.clients.producer.internals.DefaultPartitioner
         * 可以自定义
         */
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class.getName());


        /**
         * 创建生产者
         */
        KafkaProducer producer = new KafkaProducer(props);


        /**
         * 用户提供的回调，以便在服务器确认记录时执行（null 表示没有回调
         */

        //分区时会利用 这个key 和 设置的partition数进行分区

        String key;
        String value;
        ProducerRecord producerRecord;
        while (true) {
            Thread.sleep(1000);
            key = "key:" + (int) (10 * (Math.random()));

            value = "value:" + UUID.randomUUID().toString();
            producerRecord = new ProducerRecord(topic, null, null, key, value, null);

            producer.send(producerRecord, (metadata, exception) -> {

                System.out.println("执行Callback!!!! " +"\n"+
                        "metadate:"+ JSON.toJSONString(metadata)+"\n"
                        + "exception:"+JSON.toJSONString(exception));

            });
        }


    }

}
