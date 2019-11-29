package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;
import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.Trace;
import com.github.muirandy.living.artifact.diagram.domain.*;

import java.util.stream.Stream;

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
                .flatMap(s -> createLinks(s))
                .distinct()
                .forEach(l -> chain.add(l));
        return chain;
    }

    private Stream<Link> createLinks(Span span) {
        Link base = new RectangleLink(span.name);
        if (span.storage == null)
            return Stream.of(base);

        Link storage = new QueueLink(span.storage.name);
        base.connect(new Connection(storage));
        return Stream.of(base, storage);
    }
}
