package com.github.muirandy.living.artifact.domain;

import com.github.muirandy.living.artifact.api.diagram.*;
import com.github.muirandy.living.artifact.api.trace.*;

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
            return buildChain(trace, traceId);
        return new Chain(traceId);
    }

    private Chain buildChain(Trace trace, String traceId) {
        Chain chain = new Chain(traceId);
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
        else if (span instanceof ConnectSpan)
            base = new ConnectLink(span.name);
        else
            base = new RectangleLink(span.name);

        if (span.storage == null)
            return Stream.of(base);

        Link storage = createStorageLink(span);
        base.connect(createConnection(span, storage));
        return Stream.of(base, storage);
    }

    private Link createStorageLink(Span span) {
        return new KafkaTopicLink(span.storage.name);
    }

    private Connection createConnection(Span span, Link storage) {
        if (PRODUCE.equals(span.spanOperation))
            return new Connection(LinkRelationship.PRODUCER, storage);
        return new Connection(LinkRelationship.CONSUMER, storage);
    }
}
