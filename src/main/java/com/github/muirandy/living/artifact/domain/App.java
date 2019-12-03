package com.github.muirandy.living.artifact.domain;

import com.github.muirandy.living.artifact.api.diagram.Artifact;
import com.github.muirandy.living.artifact.api.diagram.ArtifactGenerator;
import com.github.muirandy.living.artifact.api.diagram.Chain;

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