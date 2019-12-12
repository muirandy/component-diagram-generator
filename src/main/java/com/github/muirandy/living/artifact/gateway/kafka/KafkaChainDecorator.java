package com.github.muirandy.living.artifact.gateway.kafka;

import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.diagram.KafkaTopicLink;
import com.github.muirandy.living.artifact.api.diagram.Link;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;

import java.util.List;

public class KafkaChainDecorator implements ChainDecorator {
    private KafkaTopicConsumer kafkaTopicConsumer;

    public KafkaChainDecorator(KafkaTopicConsumer kafkaTopicConsumer) {
        this.kafkaTopicConsumer = kafkaTopicConsumer;
    }

    @Override
    public Chain decorate(Chain chain) {
        List<Link> links = chain.getLinks();
        links.stream()
             .filter(l -> l instanceof KafkaTopicLink)
             .forEach(l -> populateLink((KafkaTopicLink) l));
        return chain;
    }

    private void populateLink(KafkaTopicLink link) {
        KafkaMessage message = kafkaTopicConsumer.getMessage(link.name);
        link.key = message.kafkaMessageKey;
        link.payload = message.kafkaMessageValue;
    }
}
