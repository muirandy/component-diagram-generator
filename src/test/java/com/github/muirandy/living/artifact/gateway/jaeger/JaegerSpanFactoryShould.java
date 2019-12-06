package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.trace.*;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JaegerSpanFactoryShould {
    private static final String EXPECTED_NAME = "CSAS_Expected_Name_7";
    private static final String EXPECTED_CONNECTOR_NAME = "AMQSinkConnector";
    private static final String NOT_WANTED = "Not wanted";
    private static final String TOPIC_NAME = "Kafka Topic Name";
    private static final String KAFKA_TOPIC_TAG_NAME = "kafka.topic";
    private static final String KAFKA_CLIENT_ID_TAG_NAME = "kafka.client.id";
    private static final String KAFKA_GROUP_ID_TAG_NAME = "kafka.group.id";
    private static final String ON_SEND = "on_send";
    private static final String SEND = "send";
    private static final String ON_CONSUME = "on_consume";
    private static final String POll = "poll";
    private static final String EXPECTED_NAME_WITH_KSQL_GROUP_ID_FLUFF =
            "_confluent-ksql-default_query_" + EXPECTED_NAME;
    private static final String EXPECTED_NAME_WITH_KSQL_CLIENT_ID_FLUFF = EXPECTED_NAME_WITH_KSQL_GROUP_ID_FLUFF +
            "-40382c9c-5dd5-4c18-b748-efe4e5e3e965-StreamThread-1-producer";
    private static final String GENERIC_PROCESS = "Generic Process";

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
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        BasicSpan span = (BasicSpan) createSpanFactory().makeSpan(makeJaegerSpanJson());

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
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.name).isEqualTo(EXPECTED_NAME);
        assertThat(span.hasStorage()).isFalse();
    }

    @Test
    void nameSpanAfterTrimmedKafkaGroupId() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_CLIENT_ID_TAG_NAME)
                                .withValue(NOT_WANTED)
                                .build())
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_GROUP_ID_TAG_NAME)
                                .withValue(EXPECTED_NAME_WITH_KSQL_GROUP_ID_FLUFF)
                                .build())
                        .build())
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.name).isEqualTo(EXPECTED_NAME);
        assertThat(span.hasStorage()).isFalse();
    }

    @Test
    void nameSpanAfterTrimmedKafkaClientId() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_CLIENT_ID_TAG_NAME)
                                .withValue(EXPECTED_NAME_WITH_KSQL_CLIENT_ID_FLUFF)
                                .build())
                        .build())
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.name).isEqualTo(EXPECTED_NAME);
        assertThat(span.hasStorage()).isFalse();
    }

    @Test
    void nameSpanAfterProcessName() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create().build())
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        BasicSpan span = (BasicSpan) createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.name).isEqualTo(GENERIC_PROCESS);
        assertThat(span.hasStorage()).isFalse();
    }

    @Test
    void includeStorageProducedToWithOnSendOperation() {
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
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.hasStorage()).isTrue();
        assertThat(span.storage.name).isEqualTo(TOPIC_NAME);
        assertThat(span.spanOperation).isEqualTo(SpanOperation.PRODUCE);
    }

    @Test
    void includeStorageProducedToWithSendOperation() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withOperationName(SEND)
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_TOPIC_TAG_NAME)
                                .withValue(TOPIC_NAME)
                                .build())
                        .build())
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.hasStorage()).isTrue();
        assertThat(span.storage.name).isEqualTo(TOPIC_NAME);
        assertThat(span.spanOperation).isEqualTo(SpanOperation.PRODUCE);
    }


    @Test
    void includeStorageConsumedFromWithOnConsumeOperation() {
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
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.hasStorage()).isTrue();
        assertThat(span.storage.name).isEqualTo(TOPIC_NAME);
        assertThat(span.spanOperation).isEqualTo(SpanOperation.CONSUME);
    }

    @Test
    void includeStorageConsumedFromWithPollOperation() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withOperationName(POll)
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_TOPIC_TAG_NAME)
                                .withValue(TOPIC_NAME)
                                .build())
                        .build())
                .withProcess("p1", GENERIC_PROCESS)
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span.hasStorage()).isTrue();
        assertThat(span.storage.name).isEqualTo(TOPIC_NAME);
        assertThat(span.spanOperation).isEqualTo(SpanOperation.CONSUME);
    }

    @Test
    void createKsqlSpan() {
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
                .withProcess("p1", "ksql-server")
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span instanceof KsqlSpan).isTrue();
    }

    @Test
    void createConnectSpanForKafkaConnectConsumer() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withOperationName(ON_CONSUME)
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_CLIENT_ID_TAG_NAME)
                                .withValue(EXPECTED_CONNECTOR_NAME)
                                .build())
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_TOPIC_TAG_NAME)
                                .withValue(TOPIC_NAME)
                                .build())
                        .build())
                .withProcess("p1", "kafka-connect-consumer")
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span instanceof ConnectSpan).isTrue();
    }

    @Test
    void createConnectSpanForKafkaConnectProducer() {
        traceJson = JaegerJsonTraceBuilder.create()
                .withSpan(JaegerJsonSpanBuilder.create()
                        .withOperationName(ON_CONSUME)
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_CLIENT_ID_TAG_NAME)
                                .withValue(EXPECTED_CONNECTOR_NAME)
                                .build())
                        .withTag(JaegerJsonTagBuilder.create()
                                .withKey(KAFKA_TOPIC_TAG_NAME)
                                .withValue(TOPIC_NAME)
                                .build())
                        .build())
                .withProcess("p1", "kafka-connect-producer")
                .build();

        Span span = createSpanFactory().makeSpan(makeJaegerSpanJson());

        assertThat(span instanceof ConnectSpan).isTrue();
    }

    private JSONObject makeJaegerSpanJson() {
        return singleTrace.getJSONArray("spans").getJSONObject(0);
    }

    private JaegerSpanFactory createSpanFactory() {
        singleTrace = new JSONObject(traceJson);
        return new JaegerSpanFactory(singleTrace);
    }
}