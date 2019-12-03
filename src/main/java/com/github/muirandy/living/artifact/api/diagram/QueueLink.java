package com.github.muirandy.living.artifact.api.diagram;

public class QueueLink extends Link {
    public QueueLink(String name) {
        super(name);
    }

    @Override
    public String toSourceString(SourceStringVisitor sourceStringVisitor) {
        return sourceStringVisitor.visit(this);
    }
}
