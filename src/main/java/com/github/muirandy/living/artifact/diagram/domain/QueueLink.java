package com.github.muirandy.living.artifact.diagram.domain;

public class QueueLink extends Link {
    public QueueLink(String name) {
        super(name);
    }

    @Override
    public String toSourceString(SourceStringVisitor sourceStringVisitor) {
        return sourceStringVisitor.visit(this);
    }
}
