package com.github.muirandy.living.artifact.gateway.kafka;

public class KafkaHeader {
    public String key;
    public String value;

    public KafkaHeader(String headersKey, String headersValue) {
        key = headersKey;
        value = headersValue;
    }
}
