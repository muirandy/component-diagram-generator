package com.github.muirandy.living.artifact.gateway.kafka;

public class KafkaHeaders {
    public String key;
    public String value;

    public KafkaHeaders(String headersKey, String headersValue) {

        key = headersKey;
        value = headersValue;
    }
}
