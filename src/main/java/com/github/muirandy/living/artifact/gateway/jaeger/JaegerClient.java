package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.OpenTracingClient;
import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.Trace;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class JaegerClient implements OpenTracingClient {
    private final String jaegerServer;
    private final int jaegerPort;
    private SpanFactory spanFactory;

    public JaegerClient(String jaegerServer, int jaegerPort) {
        this.jaegerServer = jaegerServer;
        this.jaegerPort = jaegerPort;
    }

    @Override
    public Trace obtainTrace(String jaegerTraceId) {
        String url = buildJaegerUrl(jaegerTraceId);
        JSONObject root = getJaegerTrace(url);

        if (hasErrors(root))
            return errorTrace();

        return buildTrace(root);
    }

    private Trace buildTrace(JSONObject root) {
        Trace trace = new Trace();
        JSONObject singleTrace = getSingleTrace(root);
        spanFactory = new SpanFactory(singleTrace);
        JSONArray spans = getSpans(singleTrace);
        for (int i = 0; i < spans.length(); i++)
            trace.addSpan(createSpan(spans.getJSONObject(i)));

        return trace;
    }

    private JSONArray getSpans(JSONObject singleTrace) {
        return singleTrace.getJSONArray("spans");
    }

    private JSONObject getSingleTrace(JSONObject root) {
        return root.getJSONArray("data").getJSONObject(0);
    }

    private Trace errorTrace() {
        return new Trace();
    }

    private JSONObject getJaegerTrace(String url) {
        return Unirest.get(url)
                .asJson()
                .getBody()
                .getObject();
    }

    private String buildJaegerUrl(String jaegerTraceId) {
        return jaegerServer + "/api/traces/" + jaegerTraceId;
    }

    private Span createSpan(JSONObject jaegerSpan) {
        return spanFactory.makeSpan(jaegerSpan);
    }

    private boolean hasErrors(JSONObject root) {
        return root.get("errors") != null;
    }

}
