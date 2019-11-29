package com.github.muirandy.living.artifact.api.chain;

import com.github.muirandy.living.artifact.diagram.domain.*;

import java.util.stream.Stream;

public class ChainBuilder {
    private OpenTracingClient openTracingClient;

    public ChainBuilder(OpenTracingClient openTracingClient) {
        this.openTracingClient = openTracingClient;
    }

    public Chain build(String jaegerTraceId) {
        Trace trace = openTracingClient.obtainTrace(jaegerTraceId);

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
