package com.github.muirandy.living.artifact.gateway.kafka;

import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.diagram.KafkaTopicLink;
import com.github.muirandy.living.artifact.api.diagram.Link;
import com.github.muirandy.living.artifact.api.diagram.RectangleLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaChainDecoratorShould {

    private static final String TOPIC_NAME = "Topic";
    private static final String TOPIC2_NAME = "Topic2";
    private static final String KAFKA_MESSAGE_VALUE = "Message Value";
    private static final String KAFKA_MESSAGE_KEY = "Message Key";
    private static final String KAFKA_MESSAGE_VALUE_2 = "Message Value 2";
    private static final String KAFKA_MESSAGE_KEY_2 = "Message Key 2";
    private static final String RECTANGLE_LINK_NAME = "Rectangle Link";

    @Mock
    private KafkaTopicConsumer kafkaTopicConsumer;

    private KafkaChainDecorator decorator;

    @BeforeEach
    void setUp() {
        decorator = new KafkaChainDecorator(kafkaTopicConsumer);
        when(kafkaTopicConsumer.getMessage(TOPIC_NAME)).thenReturn(Optional.of(new KafkaMessage(KAFKA_MESSAGE_KEY, KAFKA_MESSAGE_VALUE)));
        when(kafkaTopicConsumer.getMessage(TOPIC2_NAME)).thenReturn(Optional.of(new KafkaMessage(KAFKA_MESSAGE_KEY_2, KAFKA_MESSAGE_VALUE_2)));
    }

    @Test
    void populateKafkaTopicLinkWithMessageKeyAndValue() {
        Chain chain = createChain(new KafkaTopicLink(TOPIC_NAME),
                new RectangleLink(RECTANGLE_LINK_NAME),
                new KafkaTopicLink(TOPIC2_NAME));

        Chain decoratedChain = decorator.decorate(chain);

        KafkaTopicLink expectedLink = buildKafkaTopicLink(TOPIC_NAME, KAFKA_MESSAGE_KEY, KAFKA_MESSAGE_VALUE);
        Link differentTypeOfLink = buildLink(RECTANGLE_LINK_NAME);
        KafkaTopicLink expectedLink2 = buildKafkaTopicLink(TOPIC2_NAME, KAFKA_MESSAGE_KEY_2, KAFKA_MESSAGE_VALUE_2);
        assertThat(decoratedChain.getLinks()).containsExactly(expectedLink, differentTypeOfLink, expectedLink2);
    }

    private KafkaTopicLink buildKafkaTopicLink(String topicName, String kafkaMessageKey, String kafkaMessageValue) {
        KafkaTopicLink expectedLink = new KafkaTopicLink(topicName);
        expectedLink.key = kafkaMessageKey;
        expectedLink.payload = kafkaMessageValue;
        return expectedLink;
    }

    private Link buildLink(String name) {
        Link link = new RectangleLink(name);
        return link;
    }

    private Chain createChain(Link... links) {
        Chain chain = new Chain();
        Arrays.stream(links).forEach(l -> chain.add(l));
        return chain;
    }
}