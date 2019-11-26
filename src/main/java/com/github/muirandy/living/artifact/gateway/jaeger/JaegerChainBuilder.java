package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;
import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.Trace;
import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.diagram.domain.Link;
import com.github.muirandy.living.artifact.diagram.domain.RectangleLink;

public class JaegerChainBuilder implements ChainBuilder {
    private JaegerClient jaegerClient;

    public JaegerChainBuilder(JaegerClient jaegerClient) {
        this.jaegerClient = jaegerClient;
    }

    @Override
    public Chain build(String jaegerTraceId) {
        Chain chain = new Chain();
        Trace trace = jaegerClient.obtainTrace(jaegerTraceId);
        if (!trace.isEmpty()) {
            Span span = trace.spans.get(0);
            Link link = new RectangleLink(span.name);
            chain.add(link);
        }
        return chain;
    }
}
