package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.*;
import com.github.muirandy.living.artifact.diagram.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JaegerChainBuilderShould {
    private static final String JAEGER_TRACE_ID = "" + randomInt();
    private static final String SPAN_NAME = "Span Name";
    private static final String SPAN2_NAME = "Span2 Name";

    @Mock
    private JaegerClient jaegerClient;

    private Trace trace;
    private JaegerChainBuilder jaegerChainBuilder;
    private static final String TOPIC_NAME = "myQueue";

    private static int randomInt() {
        return Math.abs(new Random().nextInt());
    }

    @BeforeEach
    void setUp() {
        jaegerChainBuilder = new JaegerChainBuilder(jaegerClient);
        trace = new Trace();
        when(jaegerClient.obtainTrace(JAEGER_TRACE_ID)).thenReturn(trace);
    }

    @Test
    void returnEmptyChainForEmptyTrace() {
        Chain chain = jaegerChainBuilder.build(JAEGER_TRACE_ID);

        assertThat(chain.isEmpty()).isTrue();
    }

    @Test
    void buildLinkForSingleSpan() {
        addSpansToTrace(new Span(SPAN_NAME));

        Chain chain = jaegerChainBuilder.build(JAEGER_TRACE_ID);

        assertThat(chain.getLinks()).containsExactly(new RectangleLink(SPAN_NAME));
    }

    @Test
    void buildLinksForMultipleSpans() {
        addSpansToTrace(new Span("Span 1"), new Span("Span 2"));

        Chain chain = jaegerChainBuilder.build(JAEGER_TRACE_ID);

        assertThat(chain.getLinks()).containsExactly(new RectangleLink("Span 1"), new RectangleLink("Span 2"));
    }

    @Test
    void buildLinkForDataSinkWithinSpan() {
        Storage topic = new Topic(TOPIC_NAME);
        Span span = new Span(SPAN_NAME);
        span.addStorage(SpanOperation.PRODUCE, topic);
        addSpansToTrace(span);

        Chain chain = jaegerChainBuilder.build(JAEGER_TRACE_ID);

        RectangleLink rectangleLink = new RectangleLink(SPAN_NAME);
        QueueLink queueLink = new QueueLink(TOPIC_NAME);
        rectangleLink.connect(new Connection(queueLink));
        assertThat(chain.getLinks()).containsExactly(rectangleLink, queueLink);
    }

    @Test
    void buildSharedLinkForDataSinkAcrossTwoSpans() {
        Storage topic = new Topic(TOPIC_NAME);

        Span span = new Span(SPAN_NAME);
        span.addStorage(SpanOperation.PRODUCE, topic);

        Span span2 = new Span(SPAN2_NAME);
        span2.addStorage(SpanOperation.CONSUME, topic);

        addSpansToTrace(span, span2);

        Chain chain = jaegerChainBuilder.build(JAEGER_TRACE_ID);

        RectangleLink producerLink = new RectangleLink(SPAN_NAME);
        QueueLink queueLink = new QueueLink(TOPIC_NAME);
        RectangleLink consumerLink = new RectangleLink(SPAN2_NAME);
        producerLink.connect(new Connection(queueLink));
        consumerLink.connect(new Connection(queueLink));
        assertThat(chain.getLinks()).containsExactly(producerLink, queueLink, consumerLink);
    }

    private void addSpansToTrace(Span... spans) {
        for (Span span : spans)
            trace.addSpan(span);
    }
}