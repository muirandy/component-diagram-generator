package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.Trace;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

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
            trace.addSpan(span);
            //TODO: Storage
        }

        return trace;
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
