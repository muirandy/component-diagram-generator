package com.github.muirandy.living.artifact.main;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;
import com.github.muirandy.living.artifact.diagram.domain.Artifact;
import com.github.muirandy.living.artifact.diagram.domain.ArtifactGenerator;
import com.github.muirandy.living.artifact.diagram.domain.Chain;

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
