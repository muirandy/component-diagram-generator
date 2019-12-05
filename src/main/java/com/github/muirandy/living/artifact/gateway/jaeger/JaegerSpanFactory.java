package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.trace.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class JaegerSpanFactory {
    private static final String ON_SEND = "on_send";
    private static final String KAFKA_KSQL_PREAMBLE = "_confluent-ksql-default_query_";
    private JSONObject singleTrace;
    private Map<String, Class<? extends Span>> processes;
    private Map<String, String> processNames;
    private Map<String, Class<? extends Span>> serviceNames;

    JaegerSpanFactory(JSONObject singleTrace) {
        this.singleTrace = singleTrace;
        initialiseServiceNameMap();
        initialiseProcessMap();
        initialiseProcessNameMap();
    }

    private void initialiseServiceNameMap() {
        serviceNames = new HashMap<>();
        serviceNames.put("ksql-server", KsqlSpan.class);
    }

    private void initialiseProcessMap() {
        processes = new HashMap<>();
        JSONObject processes = singleTrace.getJSONObject("processes");
        for (String processId : processes.keySet()) {
            String processName = processes.getJSONObject(processId).getString("serviceName");
            this.processes.put(processId, serviceNames.getOrDefault(processName, BasicSpan.class));
        }
    }

    private void initialiseProcessNameMap() {
        processNames = new HashMap<>();
        JSONObject processes = singleTrace.getJSONObject("processes");
        for (String processId : processes.keySet()) {
            String processName = processes.getJSONObject(processId).getString("serviceName");
            this.processNames.put(processId, processName);
        }
    }

    Span makeSpan(JSONObject jaegerSpan) {
        JSONArray jaegerTags = jaegerSpan.getJSONArray("tags");
        Span span = createNewSpan(jaegerSpan);
        if (storageIsPresent(jaegerTags)) {
            Storage storage = readStorage(jaegerTags);
            span.addStorage(readOperation(jaegerSpan), storage);
        }
        return span;
    }

    private Span createNewSpan(JSONObject jaegerSpan) {
        JSONArray jaegerTags = jaegerSpan.getJSONArray("tags");
        String processId = readProcessIdForSpan(jaegerSpan);
        Class<? extends Span> classForSpan = processes.get(processId);
        try {
            return classForSpan.getConstructor(String.class).newInstance(readSpanName(jaegerSpan));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean storageIsPresent(JSONArray jaegerTags) {
        Optional<String> topic = readTag(jaegerTags, "kafka.topic");
        return topic.isPresent();
    }

    private Storage readStorage(JSONArray jaegerTags) {
        Optional<String> topic = readTag(jaegerTags, "kafka.topic");
        return new KafkaTopicStorage(topic.get());
    }

    private SpanOperation readOperation(JSONObject jaegerSpan) {
        if (ON_SEND.equals(jaegerSpan.getString("operationName")))
            return SpanOperation.PRODUCE;
        return SpanOperation.CONSUME;
    }

    private String readProcessIdForSpan(JSONObject jaegerSpan) {
        return jaegerSpan.getString("processID");
    }

    private String readSpanName(JSONObject jaegerSpan) {
        String name = "Unknown!!";
        JSONArray jaegerTags = jaegerSpan.getJSONArray("tags");
        Optional<String> groupId = readTag(jaegerTags, "kafka.group.id");
        Optional<String> clientId = readTag(jaegerTags, "kafka.client.id");
        if (groupId.isPresent())
            name = groupId.get();
        else {
            if (clientId.isPresent())
                name = trimPostfix(clientId.get());
            else {
                String processId = readProcessIdForSpan(jaegerSpan);
                name = processNames.get(processId);
            }
        }

        return trimPrefixKafkaName(name);
    }

    private Optional<String> readTag(JSONArray jaegerTags, String tagName) {
        for (int i = 0; i < jaegerTags.length(); i++) {
            JSONObject tag = jaegerTags.getJSONObject(i);
            if (tagName.equals(tag.getString("key")))
                return Optional.of(tag.getString("value"));
        }
        return Optional.empty();
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

    private String trimPrefixKafkaName(String name) {
        String trimmedName = name;
        if (name.startsWith(KAFKA_KSQL_PREAMBLE))
            trimmedName = name.substring(KAFKA_KSQL_PREAMBLE.length());
        return trimmedName;
    }

}
