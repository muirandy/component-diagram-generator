package com.github.muirandy.living.artifact.domain;

import com.github.muirandy.living.artifact.api.diagram.*;
import com.github.muirandy.living.artifact.api.trace.KsqlSpan;
import com.github.muirandy.living.artifact.api.trace.OpenTracingClient;
import com.github.muirandy.living.artifact.api.trace.Span;
import com.github.muirandy.living.artifact.api.trace.Trace;

import java.util.stream.Stream;

import static com.github.muirandy.living.artifact.api.trace.SpanOperation.PRODUCE;

public class ChainBuilder {
    private OpenTracingClient openTracingClient;

    public ChainBuilder(OpenTracingClient openTracingClient) {
        this.openTracingClient = openTracingClient;
    }

    public Chain build(String traceId) {
        Trace trace = openTracingClient.obtainTrace(traceId);

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
        Link base;
        if (span instanceof KsqlSpan)
            base = new KsqlLink(span.name);
        else
            base = new RectangleLink(span.name);
        if (span.storage == null)
            return Stream.of(base);

        Link storage = new QueueLink(span.storage.name);
        base.connect(createConnection(span, storage));
        return Stream.of(base, storage);
    }

    private Connection createConnection(Span span, Link storage) {
        if (PRODUCE.equals(span.spanOperation))
            return new Connection(LinkRelationship.PRODUCER, storage);
        return new Connection(LinkRelationship.CONSUMER, storage);
    }
}
