package com.github.muirandy.living.artifact.api.diagram;

public interface SourceStringVisitor {
    String visit(QueueLink link);
    String visit(ActiveMqQueueLink link);
    String visit(RectangleLink link);
    String visit(KsqlLink ksqlLink);
    String visit(KafkaTopicLink kafkaTopicLink);
    String visit(ConnectLink connectLink);
}
