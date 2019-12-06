package com.github.muirandy.living.artifact.api.diagram;

public class ConnectLink extends Link {
    public ConnectLink(String name) {
        super(name);
    }

    @Override
    public String toSourceString(SourceStringVisitor sourceStringVisitor) {
        return sourceStringVisitor.visit(this);
    }
}
