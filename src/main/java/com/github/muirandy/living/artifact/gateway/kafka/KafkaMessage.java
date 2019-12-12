package com.github.muirandy.living.artifact.gateway.kafka;

public class KafkaMessage {
    final String kafkaMessageKey;
    final String kafkaMessageValue;

    public KafkaMessage(String kafkaMessageKey, String kafkaMessageValue) {
        this.kafkaMessageKey = kafkaMessageKey;
        this.kafkaMessageValue = kafkaMessageValue;
    }
}
