package com.github.muirandy.living.artifact.diagram.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class Link {
    public final String name;
    public final List<Connection> connections = new ArrayList<>();

    public Link(String name) {
        this.name = name;
    }

    public void connect(Connection connection) {
        connections.add(connection);
    }

    public boolean hasConnections() {
        return !connections.isEmpty();
    }

    public abstract String toSourceString(SourceStringVisitor sourceStringVisitor);

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }
}
