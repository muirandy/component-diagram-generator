package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.diagram.domain.Chain;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class JaegerJsonTraceBuilder {
    private String traceId = UUID.randomUUID().toString();
    private List<JSONObject> spans = new ArrayList<>();
    private List<JSONObject> processes = new ArrayList<>();

    static JaegerJsonTraceBuilder create() {
        return new JaegerJsonTraceBuilder();
    }

    JaegerJsonTraceBuilder withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    JaegerJsonTraceBuilder withSpan(String spanJson) {
        spans.add(new JSONObject(spanJson));
        return this;
    }

    JaegerJsonTraceBuilder withProcess(String processJson) {
        processes.add(new JSONObject(processJson));
        return this;
    }

    String build() {
        JSONObject trace = new JSONObject();
        trace.put("traceID", traceId);
        trace.put("spans", new JSONArray(spans));
        trace.put("processes", new JSONArray(processes));

        return trace.toString();
    }
}
