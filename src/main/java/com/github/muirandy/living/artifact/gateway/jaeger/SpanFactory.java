package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.SpanOperation;
import com.github.muirandy.living.artifact.api.chain.Storage;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.Optional;

class SpanFactory {
    public SpanFactory(JSONObject singleTrace) {
    }

    public Span invoke(JSONArray jaegerTags) {
        Span span = new Span(readSpanName(jaegerTags));
        Optional<Storage> storage = readStorage(jaegerTags);
        if (storage.isPresent())
            span.addStorage(SpanOperation.PRODUCE, storage.get());
        return span;
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
