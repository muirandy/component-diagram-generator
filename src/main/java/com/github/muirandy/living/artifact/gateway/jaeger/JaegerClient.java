package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.trace.OpenTracingClient;
import com.github.muirandy.living.artifact.api.trace.Span;
import com.github.muirandy.living.artifact.api.trace.Trace;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JaegerClient implements OpenTracingClient {
    private final String jaegerServer;
    private JaegerSpanFactory jaegerSpanFactory;

    public JaegerClient(String jaegerServer) {
        this.jaegerServer = jaegerServer;
    }

    @Override
    public Trace obtainTrace(String jaegerTraceId) {
        String url = buildJaegerTraceUrl(jaegerTraceId);
        JSONObject root = getJaegerTrace(url);

        if (hasErrors(root))
            return errorTrace();

        return buildTrace(root);
    }

    @Override
    public List<String> obtainTraceIds() {
        String url = buildJaegerAllTracesUrl();
        JSONObject root = getJaegerTrace(url);
        return readTraceIds(root);
    }

    private List<String> readTraceIds(JSONObject root) {
        List<String> traceIds = new ArrayList<>();
        JSONArray data = root.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject trace = root.getJSONArray("data").getJSONObject(i);
            traceIds.add(trace.getString("traceID"));
        }
        return traceIds;
    }

    private String buildJaegerAllTracesUrl() {
        return jaegerServer + "/api/traces?service=kafka-connect-producer";
    }

    private Trace buildTrace(JSONObject root) {
        Trace trace = new Trace();
        JSONObject singleTrace = getSingleTrace(root);
        jaegerSpanFactory = new JaegerSpanFactory(singleTrace);
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

    private String buildJaegerTraceUrl(String jaegerTraceId) {
        return jaegerServer + "/api/traces/" + jaegerTraceId;
    }

    private Span createSpan(JSONObject jaegerSpan) {
        return jaegerSpanFactory.makeSpan(jaegerSpan);
    }

    private boolean hasErrors(JSONObject root) {
        return root.get("errors") != null;
    }

}
