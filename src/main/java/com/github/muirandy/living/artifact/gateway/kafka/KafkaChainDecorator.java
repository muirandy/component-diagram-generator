package com.github.muirandy.living.artifact.gateway.kafka;

import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;

public class KafkaChainDecorator implements ChainDecorator {
    public KafkaChainDecorator(KafkaTopicConsumer kafkaConsumerProperties) {

    }

    @Override
    public Chain decorate(Chain chain) {
        return null;
    }
}
