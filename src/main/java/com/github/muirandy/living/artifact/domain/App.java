package com.github.muirandy.living.artifact.domain;

import com.github.muirandy.living.artifact.api.diagram.Artifact;
import com.github.muirandy.living.artifact.api.diagram.ArtifactGenerator;
import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;
import com.github.muirandy.living.artifact.api.trace.OpenTracingClient;
import com.github.muirandy.living.artifact.api.trace.Trace;

public class App {

    private OpenTracingClient openTracingClient;
    private ChainBuilder chainBuilder;
    private ArtifactGenerator artifactGenerator;

    public App(OpenTracingClient openTracingClient, ChainBuilder chainBuilder, ArtifactGenerator artifactGenerator) {
        this.openTracingClient = openTracingClient;
        this.chainBuilder = chainBuilder;
        this.artifactGenerator = artifactGenerator;
    }

    public Artifact obtainArtifact(String traceId) {
        Trace trace = openTracingClient.obtainTrace(traceId);
        Chain chain = chainBuilder.build(trace);

        return artifactGenerator.generate(chain);
    }
}
