package com.github.muirandy.living.artifact.gateway.jaeger;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;
import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

class JaegerServiceTest {
    private static final String JAEGER_TRACE_ID = "0123456789abcdef";

    private Chain chain;
    private ChainBuilder jaegerChainBuilder;

    private static int jaegerPort;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        jaegerPort = wireMockServer.port();
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

    private void givenThereIsNoTracingAvailable() {
        wireMockServer.stubFor(get(urlEqualTo("/api/traces/" + JAEGER_TRACE_ID))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(500)
                        .withBodyFile("noTrace.json"))
        );
    }

    private void whenWeRunTheService() {
        JaegerClient jaegerClient = new JaegerClient(jaegerPort);
        jaegerChainBuilder = new JaegerChainBuilder(jaegerClient);
        chain = jaegerChainBuilder.build(JAEGER_TRACE_ID);
    }

    private void thenWeGetAnEmptyChainBack() {
        assertThat(chain.isEmpty()).isTrue();
    }
}
