package com.github.muirandy.living.artifact.diagram.domain;

public interface SourceStringVisitor {
    String visit(QueueLink link);
    String visit(ActiveMqQueueLink link);
    String visit(RectangleLink link);
    String visit(KsqlLink ksqlLink);
}
