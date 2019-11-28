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

import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

class JaegerServiceTest {
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
    void singleSpanWithNoStorage() {
        givenTraceAvailable("singleSpanTraceNoStorage.json");
        whenWeRunTheService();
        thenWeGetAChainWithLinksBack(singleSpan());
    }

    @Test
    void singleSpanWithStorage() {
        givenTraceAvailable("singleSpanTrace.json");
        whenWeRunTheService();
        Span singleSpan = singleSpan();
        Storage storage = new Storage("incoming.activemq");
        singleSpan.addStorage(SpanOperation.PRODUCE, storage);
        thenWeGetAChainWithLinksBack(singleSpan);
    }

    private Span singleSpan() {
        return new Span("connector-producer-activeMqSourceConnector-0");
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

    private void thenWeGetAChainWithLinksBack(Span singleSpan) {
        assertThat(chain.isEmpty()).isFalse();
        assertThat(chain.getLinks()).containsExactly(spanToLink(singleSpan));
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
}
