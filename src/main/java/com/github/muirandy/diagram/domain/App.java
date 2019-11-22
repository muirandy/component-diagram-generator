package com.github.muirandy.diagram.domain;

public class App {
    private ArtifactGenerator artifactGenerator;

    public App(ArtifactGenerator artifactGenerator) {
        this.artifactGenerator = artifactGenerator;
    }

    public Artifact run(Chain chain) {
        return artifactGenerator.generate(chain);
    }
}
