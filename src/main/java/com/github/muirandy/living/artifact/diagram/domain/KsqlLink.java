package com.github.muirandy.living.artifact.diagram.domain;

public class KsqlLink extends Link {
    public KsqlLink(String s) {
        super(s);
    }

    @Override
    public String toSourceString(SourceStringVisitor sourceStringVisitor) {
        return sourceStringVisitor.visit(this);
    }
}
