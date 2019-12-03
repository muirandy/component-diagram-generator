package com.github.muirandy.living.artifact.api.diagram;

public class RectangleLink extends Link {
    public RectangleLink(String name) {
        super(name);
    }

    @Override
    public String toSourceString(SourceStringVisitor sourceStringVisitor) {
        return sourceStringVisitor.visit(this);
    }
}
