package com.github.muirandy.gateway.plantuml;

import com.github.muirandy.diagram.domain.Artifact;
import com.github.muirandy.diagram.domain.ArtifactGenerator;
import com.github.muirandy.diagram.domain.Chain;

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
