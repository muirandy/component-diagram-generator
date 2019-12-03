package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.api.diagram.Artifact;
import com.github.muirandy.living.artifact.api.diagram.ArtifactGenerator;
import com.github.muirandy.living.artifact.api.diagram.Chain;

import java.io.ByteArrayOutputStream;

public class PlantUmlArtifactGenerator implements ArtifactGenerator {
    private final PlantUmlSourceBuilder plantUmlSourceBuilder;
    private final ComponentDiagramGenerator componentDiagramGenerator;

    public PlantUmlArtifactGenerator(PlantUmlSourceBuilder plantUmlSourceBuilder, ComponentDiagramGenerator componentDiagramGenerator) {
        this.plantUmlSourceBuilder = plantUmlSourceBuilder;
        this.componentDiagramGenerator = componentDiagramGenerator;
    }

    @Override
    public Artifact generate(Chain chain) {
        String plantUmlSource = plantUmlSourceBuilder.build(chain);
        ByteArrayOutputStream diagramOutputStream = componentDiagramGenerator.generate(plantUmlSource);
        return new Artifact(diagramOutputStream);
    }
}
