package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;
import com.github.muirandy.living.artifact.api.chain.Trace;
import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.diagram.domain.RectangleLink;

public class JaegerChainBuilder implements ChainBuilder {
    private JaegerClient jaegerClient;

    public JaegerChainBuilder(JaegerClient jaegerClient) {
        this.jaegerClient = jaegerClient;
    }

    @Override
    public Chain build(String jaegerTraceId) {
        Trace trace = jaegerClient.obtainTrace(jaegerTraceId);

        if (!trace.isEmpty())
            return buildChain(trace);
        return new Chain();
    }

    private Chain buildChain(Trace trace) {
        Chain chain = new Chain();
        trace.spans.stream()
                .map(s -> new RectangleLink(s.name))
                .forEach(l -> chain.add(l));
        return chain;
    }
}
