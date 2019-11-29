package com.github.muirandy.living.artifact.diagram.domain;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;

public class App {
    private ChainBuilder chainBuilder;
    private ArtifactGenerator artifactGenerator;

    public App(ChainBuilder chainBuilder, ArtifactGenerator artifactGenerator) {
        this.chainBuilder = chainBuilder;
        this.artifactGenerator = artifactGenerator;
    }

    public Artifact run(String traceId) {
        Chain chain = chainBuilder.build(traceId);
        return artifactGenerator.generate(chain);
    }
}
