package com.github.muirandy.living.artifact.gateway.jaeger;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class JaegerJsonSpanBuilder {
    private String traceId = UUID.randomUUID().toString();
    private String spanId = UUID.randomUUID().toString();
    private String operationName = "on_send";
    private List<JSONObject> tags = new ArrayList<>();
    private String processId = "p1";


    static JaegerJsonSpanBuilder create() {
        return new JaegerJsonSpanBuilder();
    }

    JaegerJsonSpanBuilder withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    JaegerJsonSpanBuilder withTag(String tagJson) {
        tags.add(new JSONObject(tagJson));
        return this;
    }

    JaegerJsonSpanBuilder withProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    JaegerJsonSpanBuilder withOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    String build() {
        JSONObject span = new JSONObject();
        span.put("traceID", traceId);
        span.put("spanID", spanId);
        span.put("operationName", operationName);
        span.put("tags", new JSONArray(tags));
        span.put("processID", processId);
        return span.toString();
    }
}
