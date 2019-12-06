package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.trace.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

class JaegerServiceTest {
    private static final String SPAN_NAME = "connector-producer-activeMqSourceConnector-0";
    private String JAEGER_TRACE_ID = "0123456789abcdef" + getRandomNumber();

    private static String jaegerServer;

    private static int jaegerPort;
    private static WireMockServer wireMockServer;
    private Trace trace;
    private List<String> traces;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        jaegerServer = wireMockServer.baseUrl();
        jaegerPort = wireMockServer.port();
        waitForWiremock();
    }

    private int getRandomNumber() {
        return Math.abs(new Random().nextInt());
    }

    private static void waitForWiremock() {
        while (!wireMockServer.isRunning()) ;
    }

    @BeforeEach
    void setupStub() {
        wireMockServer.resetAll();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void noTracingExists() {
        givenThereIsNoTracingAvailable();
        whenWeRunTheService();
        thenWeGetAnErrorTraceBack();
    }

    private void thenWeGetAnErrorTraceBack() {
        assertThat(trace.isEmpty()).isTrue();
    }

    @Test
    void singleSpanWithoutStorage() {
        givenTraceAvailable("singleSpanTraceNoStorage.json");
        whenWeRunTheService();
        thenWeGetATraceWithSpansBack(singleSpan(SPAN_NAME));
    }

    @Test
    void singleSpanWithStorage() {
        givenTraceAvailable("singleSpanTrace.json");

        whenWeRunTheService();

        Span singleSpan = singleSpan(SPAN_NAME);
        Storage storage = new KafkaTopicStorage("incoming.from.activemq");
        singleSpan.addStorage(SpanOperation.PRODUCE, storage);
        thenWeGetATraceWithSpansBack(singleSpan);
    }

    @Test
    void twoSpansWithoutStorage() {
        givenTraceAvailable("twoSpansTraceNoStorage.json");
        whenWeRunTheService();

        thenWeGetATraceWithSpansBack(singleSpan("Element-1"), singleSpan("Element-2"));
    }

    @Test
    void ksqlSpan() {
        givenTraceAvailable("singleKsqlSpanTrace.json");

        whenWeRunTheService();

        Span ksqlSpan = ksqlSpan("CSAS_STREAM_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID_5");
        Storage storage = new KafkaTopicStorage("STREAM_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID");
        ksqlSpan.addStorage(SpanOperation.PRODUCE, storage);
        thenWeGetATraceWithSpansBack(ksqlSpan);
    }

    @Test
    void obtainTraceIds() {
        givenTracesForServiceAreAvailable("kafkaConnectProducerServiceTraces.json");

        whenWeObtainTraces();

        thenWeGetTracesBack("5dea357c73356e28f3e96e667ec61f2d");
    }

    private Span ksqlSpan(String spanName) {
        return new KsqlSpan(spanName);
    }

    private Span singleSpan(String spanName) {
        return new BasicSpan(spanName);
    }

    private void givenThereIsNoTracingAvailable() {
        wireMockServer.stubFor(get(urlEqualTo("/api/traces/" + JAEGER_TRACE_ID))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(500)
                        .withBodyFile("noTrace.json"))
        );
    }

    private void givenTraceAvailable(String filename) {
        wireMockServer.stubFor(get(urlEqualTo("/api/traces/" + JAEGER_TRACE_ID))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile(filename))
        );
    }

    private void givenTracesForServiceAreAvailable(String filename) {
        wireMockServer.stubFor(get(urlEqualTo("/api/traces?service=kafka-connect-producer"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile(filename))
        );
    }

    private void whenWeRunTheService() {
        OpenTracingClient jaegerClient = new JaegerClient(jaegerServer);
        trace = jaegerClient.obtainTrace(JAEGER_TRACE_ID);
    }

    private void whenWeObtainTraces() {
        OpenTracingClient jaegerClient = new JaegerClient(jaegerServer);
        traces = jaegerClient.obtainTraceIds();
    }

    private void thenWeGetATraceWithSpansBack(Span... spans) {
        assertThat(trace.isEmpty()).isFalse();
        assertThat(trace.spans).containsExactly(spans);
    }

    private void thenWeGetTracesBack(String... traceIds) {
        assertThat(traces).containsExactly(traceIds);
    }
}