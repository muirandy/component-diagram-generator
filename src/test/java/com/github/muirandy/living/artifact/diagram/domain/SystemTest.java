package com.github.muirandy.living.artifact.diagram.domain;

import com.github.muirandy.living.artifact.gateway.jaeger.JaegerChainBuilder;
import com.github.muirandy.living.artifact.gateway.jaeger.JaegerClient;
import com.github.muirandy.living.artifact.gateway.plantuml.ComponentDiagramGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlArtifactGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlSourceBuilder;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlunit.assertj.XmlAssert;
import org.xmlunit.builder.Input;

import javax.xml.transform.Source;
import java.io.File;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class SystemTest {
    private static final String JAEGER_TRACE_ID = "0123456789abcdef";

    private static WireMockServer wireMockServer;
    private static int jaegerPort;
    private static String jaegerServer;

    private Artifact artifact;

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        jaegerServer = wireMockServer.baseUrl();
        jaegerPort = wireMockServer.port();
        waitForWiremock();
    }

    private static void waitForWiremock() {
        while (!wireMockServer.isRunning()) ;
    }

    @Test
    void systemBuildsDiagramFromTrace() {
        givenTraceAvailable("systemTrace.json");
        App app = new App(createJaegerChainBuilder(), createArtifaceGenerator());

        artifact = app.run(JAEGER_TRACE_ID);

        thenWeGetSystemPlantUmlDiagramBack();
    }

    private JaegerChainBuilder createJaegerChainBuilder() {
        JaegerClient jaegerClient = new JaegerClient(jaegerServer, jaegerPort);
        return new JaegerChainBuilder(jaegerClient);
    }

    private ArtifactGenerator createArtifaceGenerator() {
        ComponentDiagramGenerator componentDiagramGenerator = new ComponentDiagramGenerator();
        PlantUmlSourceBuilder plantUmlSourceBuilder = new PlantUmlSourceBuilder();
        return new PlantUmlArtifactGenerator(plantUmlSourceBuilder, componentDiagramGenerator);
    }

    private void thenWeGetSystemPlantUmlDiagramBack() {
        File emptyImage = new File(SystemTest.class.getClassLoader().getResource("system.svg").getFile());
        Source expected = Input.fromFile(emptyImage).build();

        String svg = getResultingSvg();

        XmlAssert.assertThat(svg).and(expected)
                .ignoreWhitespace()
                .ignoreComments()
                .areIdentical();
    }

    private String getResultingSvg() {
        return new String(artifact.document.toByteArray(), Charset.forName("UTF-8"));
    }

    private void givenTraceAvailable(String filename) {
        wireMockServer.stubFor(get(urlEqualTo("/api/traces/" + JAEGER_TRACE_ID))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile(filename))
        );
    }

    @BeforeEach
    void setupStub() {
        wireMockServer.resetAll();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

}

