package com.github.muirandy.living.artifact.api.diagram;

public class KafkaTopicLink extends Link {
    public KafkaTopicLink(String name) {
        super(name);
    }

    @Override
    public String toSourceString(SourceStringVisitor sourceStringVisitor) {
        return sourceStringVisitor.visit(this);
    }
}
