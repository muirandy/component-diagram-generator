package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.SpanOperation;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpanFactoryShould {
    private static final String EXPECTED_NAME = "Expected Name";
    private static final String NOT_WANTED = "Not wanted";
    private static final String TOPIC_NAME = "Kafka Topic Name";
    private static final String KAFKA_TOPIC_TAG_NAME = "kafka.topic";
    private static final String KAFKA_CLIENT_ID_TAG_NAME = "kafka.client.id";
    private static final String KAFKA_GROUP_ID_TAG_NAME = "kafka.group.id";
    private static final String ON_SEND = "on_send";
    private static final String ON_CONSUME = "on_consume";
    private JSONObject singleTrace;
    private String traceJson;

    @Test
    void nameSpanAfterKafkaClientId() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey("kafka.client.id")
                                .withValue(EXPECTED_NAME)
                                .build())
                        .build())
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.name).isEqualTo(EXPECTED_NAME);
        assertThat(span.hasStorage()).isFalse();
    }

    @Test
    void nameSpanAfterKafkaGroupIdIfPresent() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_CLIENT_ID_TAG_NAME)
                                .withValue(NOT_WANTED)
                                .build())
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_GROUP_ID_TAG_NAME)
                                .withValue(EXPECTED_NAME)
                                .build())
                        .build())
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.name).isEqualTo(EXPECTED_NAME);
        assertThat(span.hasStorage()).isFalse();
    }

    @Test
    void includeStorageProducedTo() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withOperationName(ON_SEND)
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_CLIENT_ID_TAG_NAME)
                                .withValue(EXPECTED_NAME)
                                .build())
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_TOPIC_TAG_NAME)
                                .withValue(TOPIC_NAME)
                                .build())
                        .build())
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.hasStorage()).isTrue();
        assertThat(span.storage.name).isEqualTo(TOPIC_NAME);
        assertThat(span.spanOperation).isEqualTo(SpanOperation.PRODUCE);
    }

    @Test
    void includeStorageConsumedFrom() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withOperationName(ON_CONSUME)
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_CLIENT_ID_TAG_NAME)
                                .withValue(EXPECTED_NAME)
                                .build())
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_TOPIC_TAG_NAME)
                                .withValue(TOPIC_NAME)
                                .build())
                        .build())
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.hasStorage()).isTrue();
        assertThat(span.storage.name).isEqualTo(TOPIC_NAME);
        assertThat(span.spanOperation).isEqualTo(SpanOperation.CONSUME);
    }

    private JSONObject makeJaegerSpanJson() {
        return singleTrace.getJSONArray("spans").getJSONObject(0);
    }

    private SpanFactory createSpanFactory() {
        singleTrace = new JSONObject(traceJson);
        return new SpanFactory(singleTrace);
    }
}