package com.github.muirandy.living.artifact.gateway.jaeger;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.*;

class JaegerJsonTraceBuilder {
    private String traceId = UUID.randomUUID().toString();
    private List<JSONObject> spans = new ArrayList<>();
    private Map<String, Object> processes = new HashMap<>();

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

    JaegerJsonTraceBuilder withProcess(String processId, String processName) {
        Map<String,String> process = new HashMap<>();
        process.put("serviceName", processName);
        processes.put(processId, process);
        return this;
    }

    String build() {
        JSONObject trace = new JSONObject();
        trace.put("traceID", traceId);
        trace.put("spans", new JSONArray(spans));
        trace.put("processes", new JSONObject(processes));

        return trace.toString();
    }
}
