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
    private ChainDecorator chainDecorator;
    private ArtifactGenerator artifactGenerator;

    public App(OpenTracingClient openTracingClient, ChainBuilder chainBuilder, ArtifactGenerator artifactGenerator) {
        this.openTracingClient = openTracingClient;
        this.chainBuilder = chainBuilder;
        this.artifactGenerator = artifactGenerator;
    }

    public App(OpenTracingClient openTracingClient, ChainBuilder chainBuilder, ChainDecorator chainDecorator, ArtifactGenerator artifactGenerator) {
        this.openTracingClient = openTracingClient;
        this.chainBuilder = chainBuilder;
        this.chainDecorator = chainDecorator;
        this.artifactGenerator = artifactGenerator;
    }

    public Artifact obtainArtifact(String traceId) {
        Trace trace = openTracingClient.obtainTrace(traceId);
        Chain chain = chainBuilder.build(trace);
        chain = enhanceChain(chain);

        return artifactGenerator.generate(chain);
    }

    private Chain enhanceChain(Chain chain) {
        if (chainDecorator != null)
            return chainDecorator.decorate(chain);
        return chain;
    }
}
