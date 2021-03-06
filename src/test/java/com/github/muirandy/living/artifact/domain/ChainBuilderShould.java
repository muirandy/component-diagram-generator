package com.github.muirandy.living.artifact.domain;

import com.github.muirandy.living.artifact.api.diagram.*;
import com.github.muirandy.living.artifact.api.trace.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static com.github.muirandy.living.artifact.api.diagram.LinkRelationship.CONSUMER;
import static com.github.muirandy.living.artifact.api.diagram.LinkRelationship.PRODUCER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChainBuilderShould {
    private static final String JAEGER_TRACE_ID = "" + randomInt();
    private static final String SPAN_NAME = "Span Name";
    private static final String SPAN2_NAME = "Span2 Name";

    @Mock
    private OpenTracingClient jaegerClient;

    private Trace trace;
    private ChainBuilder chainBuilder;
    private static final String TOPIC_NAME = "myQueue";

    private static int randomInt() {
        return Math.abs(new Random().nextInt());
    }

    @BeforeEach
    void setUp() {
        chainBuilder = new ChainBuilder();
        trace = new Trace();
    }

    @Test
    void returnEmptyChainForEmptyTrace() {
        Chain chain = chainBuilder.build(trace);

        assertThat(chain.isEmpty()).isTrue();
    }

    @Test
    void buildLinkForSingleSpan() {
        addSpansToTrace(new BasicSpan(SPAN_NAME));

        Chain chain = chainBuilder.build(trace);

        assertThat(chain.getLinks()).containsExactly(new RectangleLink(SPAN_NAME));
    }

    @Test
    void buildKsqlLinkForKsqlSpan() {
        addSpansToTrace(new KsqlSpan(SPAN_NAME));

        Chain chain = chainBuilder.build(trace);

        assertThat(chain.getLinks()).containsExactly(new KsqlLink(SPAN_NAME));
    }

    @Test
    void buildConnectLinkForConnectSpan() {
        addSpansToTrace(new ConnectSpan(SPAN_NAME));

        Chain chain = chainBuilder.build(trace);

        assertThat(chain.getLinks()).containsExactly(new ConnectLink(SPAN_NAME));
    }

    @Test
    void buildLinksForMultipleSpans() {
        addSpansToTrace(new BasicSpan("Span 1"), new BasicSpan("Span 2"));

        Chain chain = chainBuilder.build(trace);

        assertThat(chain.getLinks()).containsExactly(new RectangleLink("Span 1"), new RectangleLink("Span 2"));
    }

    @Test
    void buildLinkForDataSinkWithinSpan() {
        Storage topic = new KafkaTopicStorage(TOPIC_NAME);
        Span span = new BasicSpan(SPAN_NAME);
        span.addStorage(SpanOperation.PRODUCE, topic);
        addSpansToTrace(span);

        Chain chain = chainBuilder.build(trace);

        RectangleLink rectangleLink = new RectangleLink(SPAN_NAME);
        KafkaTopicLink kafkaTopicLink = new KafkaTopicLink(TOPIC_NAME);
        rectangleLink.connect(new Connection(PRODUCER, kafkaTopicLink));
        assertThat(chain.getLinks()).containsExactly(rectangleLink, kafkaTopicLink);
    }

    @Test
    void buildSharedLinkForDataSinkAcrossTwoSpans() {
        Storage topic = new KafkaTopicStorage(TOPIC_NAME);

        Span span = new BasicSpan(SPAN_NAME);
        span.addStorage(SpanOperation.PRODUCE, topic);

        Span span2 = new BasicSpan(SPAN2_NAME);
        span2.addStorage(SpanOperation.CONSUME, topic);

        addSpansToTrace(span, span2);

        Chain chain = chainBuilder.build(trace);

        RectangleLink producerLink = new RectangleLink(SPAN_NAME);
        KafkaTopicLink kafkaTopicLink = new KafkaTopicLink(TOPIC_NAME);
        RectangleLink consumerLink = new RectangleLink(SPAN2_NAME);
        producerLink.connect(new Connection(PRODUCER, kafkaTopicLink));
        consumerLink.connect(new Connection(CONSUMER, kafkaTopicLink));
        assertThat(chain.getLinks()).containsExactly(producerLink, kafkaTopicLink, consumerLink);
    }

    private void addSpansToTrace(Span... spans) {
        for (Span span : spans)
            trace.addSpan(span);
    }
}