package com.github.muirandy.living.artifact.api.chain;

public class Span {
    public String name;
    public SpanOperation spanOperation;
    public Storage storage;

    public Span(String name) {
        this.name = name;
    }

    public void addStorage(SpanOperation spanOperation, Storage storage) {
        this.spanOperation = spanOperation;
        this.storage = storage;
    }

    public boolean hasStorage() {
        return storage != null;
    }
}
