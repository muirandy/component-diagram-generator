package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;
import com.github.muirandy.living.artifact.api.chain.Span;
import com.github.muirandy.living.artifact.api.chain.SpanOperation;
import com.github.muirandy.living.artifact.api.chain.Storage;
import com.github.muirandy.living.artifact.diagram.domain.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

class JaegerServiceTest {
    private static final String SPAN_NAME = "connector-producer-activeMqSourceConnector-0";
    private String JAEGER_TRACE_ID = "0123456789abcdef" + getRandomNumber();

    private Chain chain;

    private ChainBuilder jaegerChainBuilder;
    private static String jaegerServer;

    private static int jaegerPort;
    private static WireMockServer wireMockServer;

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
        thenWeGetAnEmptyChainBack();
    }

    @Test
    void singleSpanWithoutStorage() {
        givenTraceAvailable("singleSpanTraceNoStorage.json");
        whenWeRunTheService();
        thenWeGetAChainWithLinksBack(singleSpan(SPAN_NAME));
    }

    @Test
    void singleSpanWithStorage() {
        givenTraceAvailable("singleSpanTrace.json");
        whenWeRunTheService();
        Span singleSpan = singleSpan(SPAN_NAME);
        Storage storage = new Storage("incoming.activemq");
        singleSpan.addStorage(SpanOperation.PRODUCE, storage);
        thenWeGetAChainWithLinksBack(singleSpan);
    }

    @Test
    void twoSpansWithoutStorage() {
        givenTraceAvailable("twoSpansTraceNoStorage.json");
        whenWeRunTheService();
        thenWeGetAChainWithLinksBack(singleSpan("Element-1"), singleSpan("Element-2"));
    }

    private Span singleSpan(String spanName) {
        return new Span(spanName);
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
        JaegerClient jaegerClient = new JaegerClient(jaegerServer, jaegerPort);
        jaegerChainBuilder = new JaegerChainBuilder(jaegerClient);
        chain = jaegerChainBuilder.build(JAEGER_TRACE_ID);
    }

    private void thenWeGetAnEmptyChainBack() {
        assertThat(chain.isEmpty()).isTrue();
    }

    private void thenWeGetAChainWithLinksBack(Span... spans) {
        assertThat(chain.isEmpty()).isFalse();
        assertThat(chain.getLinks()).containsExactly(spansToLinks(spans));
    }

    private Link[] spanToLink(Span singleSpan) {
        Link link = new RectangleLink(singleSpan.name);
        if (singleSpan.hasStorage()) {
            Link storageLink = new QueueLink(singleSpan.storage.name);
            link.connect(new Connection(storageLink));
            return new Link[]{link, storageLink};
        }
        return new Link[]{link};
    }

    private Link[] spansToLinks(Span... spans) {
        List<Link> links = new ArrayList<>();
        for(Span span: spans)
            links.addAll(Arrays.asList(spanToLink(span)));
        return links.toArray(new Link[]{});
    }
}
