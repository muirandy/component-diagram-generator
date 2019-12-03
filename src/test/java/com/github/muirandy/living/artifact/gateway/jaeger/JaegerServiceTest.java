package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.trace.*;
//import com.github.muirandy.living.artifact.api.diagram.*;
//import com.github.muirandy.living.artifact.domain.ChainBuilder;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//import static com.github.muirandy.living.artifact.api.diagram.LinkRelationship.CONSUMER;
//import static com.github.muirandy.living.artifact.api.diagram.LinkRelationship.PRODUCER;
import static com.github.muirandy.living.artifact.api.diagram.LinkRelationship.PRODUCER;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

class JaegerServiceTest {
    private static final String SPAN_NAME = "connector-producer-activeMqSourceConnector-0";
    private String JAEGER_TRACE_ID = "0123456789abcdef" + getRandomNumber();

//    private Chain chain;

    //    private ChainBuilder chainBuilder;
    private static String jaegerServer;

    private static int jaegerPort;
    private static WireMockServer wireMockServer;
    private Trace trace;

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

    private void thenWeGetATraceWithSpansBack(Span... spans) {
        assertThat(trace.isEmpty()).isFalse();
        assertThat(trace.spans).containsExactly(spans);
    }

    @Test
    void singleSpanWithStorage() {
        givenTraceAvailable("singleSpanTrace.json");

        whenWeRunTheService();

        Span singleSpan = singleSpan(SPAN_NAME);
        Storage storage = new Storage("incoming.activemq");
        singleSpan.addStorage(SpanOperation.PRODUCE, storage);
        thenWeGetATraceWithSpansBack(singleSpan);
    }

    @Test
    void twoSpansWithoutStorage() {
        givenTraceAvailable("twoSpansTraceNoStorage.json");
        whenWeRunTheService();

        thenWeGetATraceWithSpansBack(singleSpan("Element-1"), singleSpan("Element-2"));
    }

//    @Test
//    void twoSpansWithSharedStorage() {
//        givenTraceAvailable("twoSpansTraceSharedStorage.json");
//
//        whenWeRunTheService();
//
//        Link link1 = new RectangleLink("Element-1");
//        Link queueLink = new QueueLink("incoming.activemq");
//        Link link3 = new RectangleLink("Element-2");
//        link1.connect(new Connection(PRODUCER, queueLink));
//        link3.connect(new Connection(CONSUMER, queueLink));
//        thenWeGetAChainWithLinksBack(link1, queueLink, link3);
//    }
//
//    @Test
//    void ksqlSpan() {
//        givenTraceAvailable("singleKsqlSpanTrace.json");
//
//        whenWeRunTheService();
//
//        KsqlLink ksqlLink = new KsqlLink("CSAS_STREAM_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID_5");
//        QueueLink queueLink = new QueueLink("STREAM_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID");
//        ksqlLink.connect(new Connection(PRODUCER, queueLink));
//        thenWeGetAChainWithLinksBack(ksqlLink, queueLink);
//    }

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

    private void whenWeRunTheService() {
        OpenTracingClient jaegerClient = new JaegerClient(jaegerServer, jaegerPort);
        trace = jaegerClient.obtainTrace(JAEGER_TRACE_ID);
    }
//
//
//    private void thenWeGetAChainWithLinksBack(Span... spans) {
//        assertThat(chain.isEmpty()).isFalse();
//        assertThat(chain.getLinks()).containsExactly(spansToLinks(spans));
//    }
//
//    private void thenWeGetAChainWithLinksBack(Link... links) {
//        assertThat(chain.isEmpty()).isFalse();
//        assertThat(chain.getLinks()).containsExactly(links);
//    }
//
//    private Link[] spanToLink(Span singleSpan) {
//        Link link = new RectangleLink(singleSpan.name);
//        if (singleSpan.hasStorage()) {
//            Link storageLink = new QueueLink(singleSpan.storage.name);
//            link.connect(new Connection(PRODUCER, storageLink));
//            return new Link[]{link, storageLink};
//        }
//        return new Link[]{link};
//    }
//
//    private Link[] spansToLinks(Span... spans) {
//        List<Link> links = new ArrayList<>();
//        for(Span span: spans)
//            links.addAll(Arrays.asList(spanToLink(span)));
//        return links.toArray(new Link[]{});
//    }
}
