package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.SpanOperation;
import com.github.muirandy.living.artifact.api.chain.Storage;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.Optional;

class JaegerSpanFactory {
    private static final String ON_SEND = "on_send";
    private static final String KAFKA_KSQL_PREAMBLE = "_confluent-ksql-default_query_";
    private JSONObject singleTrace;

    JaegerSpanFactory(JSONObject singleTrace) {
        this.singleTrace = singleTrace;
    }

    Span makeSpan(JSONObject jaegerSpan) {
        JSONArray jaegerTags = jaegerSpan.getJSONArray("tags");
        Span span = createNewSpan(jaegerTags);
        Optional<Storage> storage = readStorage(jaegerTags);
        if (storage.isPresent())
            span.addStorage(readOperation(jaegerSpan), storage.get());
        return span;
    }

    private SpanOperation readOperation(JSONObject jaegerSpan) {
        if (ON_SEND.equals(jaegerSpan.getString("operationName")))
            return SpanOperation.PRODUCE;
        return SpanOperation.CONSUME;
    }

    private Span createNewSpan(JSONArray jaegerTags) {
        return new Span(readSpanName(jaegerTags));
    }

    private String readSpanName(JSONArray jaegerTags) {
        String name = "Unknown!!";
        Optional<String> groupId = readTag(jaegerTags, "kafka.group.id");
        if (groupId.isPresent())
            name = groupId.get();
        else {
            Optional<String> clientId = readTag(jaegerTags, "kafka.client.id");
            if (clientId.isPresent())
                name = trimPostfix(clientId.get());
        }

        return trimPrefixKafkaName(name);
    }

    private String trimPrefixKafkaName(String name) {
        String trimmedName = name;
        if (name.startsWith(KAFKA_KSQL_PREAMBLE))
            trimmedName = name.substring(KAFKA_KSQL_PREAMBLE.length());
        return trimmedName;
    }

    private String trimPostfix(String name) {
        String trimmedName = name;
        int lastUnderscore = trimmedName.lastIndexOf("_");
        int postfixStartPosition = trimmedName.indexOf("-", lastUnderscore);
        if (postfixStartPosition != -1) {
            String postfix = trimmedName.substring(postfixStartPosition);
            if (postfix.contains("-StreamThread-")) {
                trimmedName = trimmedName.substring(0, postfixStartPosition);
            }
        }
        return trimmedName;
    }

    private Optional<Storage> readStorage(JSONArray jaegerTags) {
        Optional<String> topic = readTag(jaegerTags, "kafka.topic");
        if (topic.isPresent())
            return Optional.of(new Storage(topic.get()));
        return Optional.empty();
    }

    private Optional<String> readTag(JSONArray jaegerTags, String tagName) {
        for (int i = 0; i < jaegerTags.length(); i++) {
            JSONObject tag = jaegerTags.getJSONObject(i);
            if (tagName.equals(tag.getString("key")))
                return Optional.of(tag.getString("value"));
        }
        return Optional.empty();
    }
}
