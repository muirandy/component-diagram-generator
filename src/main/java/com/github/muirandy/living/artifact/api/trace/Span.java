package com.github.muirandy.living.artifact.api.trace;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class Span {
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

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
