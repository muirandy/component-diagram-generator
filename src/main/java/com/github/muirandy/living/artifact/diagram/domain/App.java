package com.github.muirandy.living.artifact.diagram.domain;

import com.github.muirandy.living.artifact.gateway.jaeger.JaegerChainBuilder;

public class App {
    private JaegerChainBuilder jaegerChainBuilder;
    private ArtifactGenerator artifactGenerator;

    public App(JaegerChainBuilder jaegerChainBuilder, ArtifactGenerator artifactGenerator) {
        this.jaegerChainBuilder = jaegerChainBuilder;
        this.artifactGenerator = artifactGenerator;
    }

    public Artifact run(String traceId) {
        Chain chain = jaegerChainBuilder.build(traceId);
        return artifactGenerator.generate(chain);
    }
}
