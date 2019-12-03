package com.github.muirandy.living.artifact.api.diagram;

public class ActiveMqQueueLink extends QueueLink {
    public ActiveMqQueueLink(String name) {
        super(name);
    }

    @Override
    public String toSourceString(SourceStringVisitor sourceStringVisitor) {
        return sourceStringVisitor.visit(this);
    }
}
