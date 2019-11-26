package com.github.muirandy.living.artifact.diagram.domain;

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
}
