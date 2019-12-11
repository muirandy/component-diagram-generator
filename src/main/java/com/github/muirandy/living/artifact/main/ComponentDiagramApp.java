package com.github.muirandy.living.artifact.main;

import com.github.muirandy.living.artifact.api.diagram.Artifact;
import com.github.muirandy.living.artifact.api.diagram.ArtifactGenerator;
import com.github.muirandy.living.artifact.api.trace.OpenTracingClient;
import com.github.muirandy.living.artifact.domain.App;
import com.github.muirandy.living.artifact.domain.ChainBuilder;
import com.github.muirandy.living.artifact.gateway.jaeger.JaegerClient;
import com.github.muirandy.living.artifact.gateway.plantuml.ComponentDiagramGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlArtifactGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlSourceBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ComponentDiagramApp {
    private static String jaegerServer;
    private List<String> existingTraceIds;

    public static void main(String[] args) {
        ComponentDiagramApp componentDiagramApp = new ComponentDiagramApp(args);
    }

    public ComponentDiagramApp(String[] args) {
        readArgs(args);
        existingTraceIds = new ArrayList<>();
    }

    public void initialise() {
        existingTraceIds = createJaegerClient().obtainTraceIds();
    }

    public ByteArrayOutputStream drawComponentDiagram() {
        App app = new App(createOpenTracingClient(),
                createJaegerChainBuilder(),
                createArtifactGenerator());

        String traceId = getNewTraceId();
        Artifact artifact = app.obtainArtifact(traceId);
        return artifact.document;
    }

    private OpenTracingClient createOpenTracingClient() {
        return createJaegerClient();
    }

    private String getNewTraceId() {
        List<String> allTraceIds = createJaegerClient().obtainTraceIds();
        allTraceIds.removeAll(existingTraceIds);
        return allTraceIds.get(0);
    }

    private void readArgs(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("There must be 1 argument: jaegerServer(eg: http://jaeger:16686)");

        jaegerServer = args[0];
    }

    private ChainBuilder createJaegerChainBuilder() {
        return new ChainBuilder();
    }

    private JaegerClient createJaegerClient() {
        return new JaegerClient(jaegerServer);
    }

    private ArtifactGenerator createArtifactGenerator() {
        ComponentDiagramGenerator componentDiagramGenerator = new ComponentDiagramGenerator();
        PlantUmlSourceBuilder plantUmlSourceBuilder = new PlantUmlSourceBuilder();
        return new PlantUmlArtifactGenerator(plantUmlSourceBuilder, componentDiagramGenerator);
    }
}