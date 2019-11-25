package com.github.muirandy.living.artifact.diagram.domain;

public class App {
    private ArtifactGenerator artifactGenerator;

    public App(ArtifactGenerator artifactGenerator) {
        this.artifactGenerator = artifactGenerator;
    }

    public Artifact run(Chain chain) {
        return artifactGenerator.generate(chain);
    }
}
