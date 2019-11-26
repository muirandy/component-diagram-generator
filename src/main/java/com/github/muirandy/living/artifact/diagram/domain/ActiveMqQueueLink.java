package com.github.muirandy.living.artifact.diagram.domain;

public class ActiveMqQueueLink extends QueueLink {
    public ActiveMqQueueLink(String name) {
        super(name);
    }

    @Override
    public String toSourceString(SourceStringVisitor sourceStringVisitor) {
        return sourceStringVisitor.visit(this);
    }
}
