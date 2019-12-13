package com.github.muirandy.living.artifact.gateway.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;

public class KafkaTopicConsumer {

    private Properties kafkaProperties;
    private KafkaHeader header;
    private KafkaConsumer<String, String> kafkaConsumer;

    public KafkaTopicConsumer(Properties kafkaProperties, KafkaHeader header) {
        this.kafkaProperties = kafkaProperties;
        this.header = header;
        kafkaConsumer = new KafkaConsumer<>(kafkaProperties);
    }

    public Optional<KafkaMessage> getMessage(String topicName) {
        ConsumerRecords<String, String> consumerRecords = pollForResults(topicName);
        for (ConsumerRecord<String, String> record : consumerRecords.records(topicName))
            if (matches(record))
                return Optional.of(new KafkaMessage(record.key(), record.value()));

        return Optional.empty();
    }

    private boolean matches(ConsumerRecord<String, String> consumerRecord) {
        return Arrays.stream(consumerRecord.headers().toArray())
                          .filter(h -> h.key().equals(header.key))
                          .anyMatch(h -> new String(h.value()).equals(header.value));
    }

    private ConsumerRecords<String, String> pollForResults(String topicName) {
        subscribeToTopic(topicName);
        Duration duration = Duration.ofSeconds(4);
        ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(duration);
        kafkaConsumer.unsubscribe();
        return consumerRecords;
    }

    private void subscribeToTopic(String topicName) {
        kafkaConsumer.subscribe(Collections.singletonList(topicName));
    }
}