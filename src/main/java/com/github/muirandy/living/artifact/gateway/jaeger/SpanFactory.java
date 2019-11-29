package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.SpanOperation;
import com.github.muirandy.living.artifact.api.chain.Storage;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.Optional;

class SpanFactory {
    private static final String ON_SEND = "on_send";
    private JSONObject singleTrace;

    SpanFactory(JSONObject singleTrace) {
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
        Optional<String> groupId = readTag(jaegerTags, "kafka.group.id");
        if (groupId.isPresent())
            return groupId.get();

        Optional<String> clientId = readTag(jaegerTags, "kafka.client.id");
        if (clientId.isPresent())
            return clientId.get();

        return "Unknown!!";
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
