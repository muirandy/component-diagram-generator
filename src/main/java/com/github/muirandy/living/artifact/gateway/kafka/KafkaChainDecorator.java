package com.github.muirandy.living.artifact.gateway.kafka;

import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;

import java.util.Properties;

public class KafkaChainDecorator implements ChainDecorator {
    public KafkaChainDecorator(Properties kafkaConsumerProperties) {

    }

    @Override
    public Chain decorate(Chain chain) {
        return null;
    }
}
