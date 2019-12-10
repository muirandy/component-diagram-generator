package com.github.muirandy.living.artifact.domain;

import com.github.muirandy.living.artifact.api.diagram.Artifact;
import com.github.muirandy.living.artifact.api.diagram.ArtifactGenerator;
import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;

public class App {
    private ChainBuilder chainBuilder;
    private ChainDecorator chainDecorator;
    private ArtifactGenerator artifactGenerator;

    public App(ChainBuilder chainBuilder, ArtifactGenerator artifactGenerator) {
        this.chainBuilder = chainBuilder;
        this.artifactGenerator = artifactGenerator;
    }

    public App(ChainBuilder chainBuilder, ChainDecorator chainDecorator, ArtifactGenerator artifactGenerator) {
        this.chainBuilder = chainBuilder;
        this.chainDecorator = chainDecorator;
        this.artifactGenerator = artifactGenerator;
    }

    public Artifact obtainTrace(String traceId) {
        Chain chain = chainBuilder.build(traceId);

        return artifactGenerator.generate(chain);
    }
}
