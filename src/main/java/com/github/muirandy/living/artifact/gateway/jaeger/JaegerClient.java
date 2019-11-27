package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.SpanOperation;
import com.github.muirandy.living.artifact.api.chain.Storage;
import com.github.muirandy.living.artifact.api.chain.Trace;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.Optional;

public class JaegerClient {
    private final String jaegerServer;
    private final int jaegerPort;

    public JaegerClient(String jaegerServer, int jaegerPort) {
        this.jaegerServer = jaegerServer;
        this.jaegerPort = jaegerPort;
    }

    public Trace obtainTrace(String jaegerTraceId) {
        String url = jaegerServer + "/api/traces/" + jaegerTraceId;
        JSONObject root = Unirest.get(url)
                .asJson()
                .getBody()
                .getObject();

        Trace trace = new Trace();
        if (hasErrors(root))
            return trace;

        JSONObject singleTrace = root.getJSONArray("data").getJSONObject(0);

        JSONArray spans = singleTrace.getJSONArray("spans");

        for (int i = 0; i < spans.length(); i++) {
            JSONObject jaegerSpan = spans.getJSONObject(i);
            JSONArray jaegerTags = jaegerSpan.getJSONArray("tags");
            String spanName = readSpanName(jaegerTags);

            Span span = new Span(spanName);

            Optional<Storage> storage = readStorage(jaegerTags);
            if (storage.isPresent())
                span.addStorage(SpanOperation.PRODUCE, storage.get());

            trace.addSpan(span);
        }

        return trace;
    }

    private Optional<Storage> readStorage(JSONArray jaegerTags) {
        for (int i = 0; i < jaegerTags.length(); i++) {
            JSONObject tag = jaegerTags.getJSONObject(i);
            if ("kafka.topic".equals(tag.getString("key")))
                return Optional.of(new Storage(tag.getString("value")));
        }
        return Optional.empty();
    }

    private boolean hasErrors(JSONObject root) {
        return root.get("errors") != null;
//        JSONArray errors = root.getJSONArray("errors");
//        return !errors.isEmpty();
    }

    private String readSpanName(JSONArray jaegerTags) {
        return "HardCodedSpanName";
    }
}
