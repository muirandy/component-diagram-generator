package com.github.muirandy.living.artifact.gateway.kafka;

import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.diagram.KafkaTopicLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaChainDecoratorShould {

    private static final String TOPIC_NAME = "Topic";
    private static final String KAFKA_MESSAGE_VALUE = "Message Value";
    private static final String KAFKA_MESSAGE_KEY = "Message Key";
    private static final String HEADERS_KEY = "Headers key";
    private static final String HEADERS_VALUE = "Headers Value";

    @Mock
    private KafkaTopicConsumer kafkaTopicConsumer;

    private KafkaChainDecorator decorator;

    @BeforeEach
    void setUp() {
        decorator = new KafkaChainDecorator(kafkaTopicConsumer);

    }

    @Test
    void populateKafkaTopicLinkWithMessageKeyAndValue() {
        KafkaHeaders headers = new KafkaHeaders(HEADERS_KEY, HEADERS_VALUE);
        when(kafkaTopicConsumer.getMessage(TOPIC_NAME, headers)).thenReturn(new KafkaMessage(KAFKA_MESSAGE_KEY, KAFKA_MESSAGE_VALUE));

        Chain chain = new Chain();
        chain.add(new KafkaTopicLink(TOPIC_NAME));

        Chain decoratedChain = decorator.decorate(chain);

        KafkaTopicLink expectedLink = new KafkaTopicLink(TOPIC_NAME);
        expectedLink.key = KAFKA_MESSAGE_KEY;
        expectedLink.payload = KAFKA_MESSAGE_VALUE;
        assertThat(decoratedChain.getLinks()).containsExactly(expectedLink);
    }
}