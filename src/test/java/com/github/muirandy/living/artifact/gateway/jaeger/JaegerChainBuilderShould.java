package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.Trace;
import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.diagram.domain.Link;
import com.github.muirandy.living.artifact.diagram.domain.RectangleLink;
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

    @Mock
    private JaegerClient jaegerClient;

    private Trace trace;
    private JaegerChainBuilder jaegerChainBuilder;

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

        assertThat(chain.getSize()).isEqualTo(1);
        Link link = chain.getLinks().get(0);
        assertThat(link.name).isEqualTo(SPAN_NAME);
        assertThat(link instanceof RectangleLink);
    }

    private void addSpansToTrace(Span... spans) {
        for (Span span : spans)
            trace.addSpan(span);
    }
}