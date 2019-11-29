package com.github.muirandy.living.artifact.gateway.jaeger;

import kong.unirest.json.JSONObject;

class JaegerJsonTagBuilder {
    private String key = "default key";
    private String type = "string";
    private String value = "default value";

    static JaegerJsonTagBuilder create() {
        return new JaegerJsonTagBuilder();
    }

    JaegerJsonTagBuilder withKey(String key) {
        this.key = key;
        return this;
    }

    JaegerJsonTagBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    String build() {
        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("type", type);
        json.put("value", value);
        return json.toString();
    }
}
