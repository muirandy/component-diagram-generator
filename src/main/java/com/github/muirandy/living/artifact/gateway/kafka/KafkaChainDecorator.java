package com.github.muirandy.living.artifact.gateway.kafka;

import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.diagram.KafkaTopicLink;
import com.github.muirandy.living.artifact.api.diagram.Link;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;

import java.util.List;
import java.util.Optional;

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
        Optional<KafkaMessage> message = kafkaTopicConsumer.getMessage(link.name);
        if (message.isPresent()) {
            link.key = message.get().kafkaMessageKey;
            link.payload = message.get().kafkaMessageValue;
        }
    }
}
