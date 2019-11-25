package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.Artifact;
import com.github.muirandy.living.artifact.diagram.domain.Chain;
import net.sourceforge.plantuml.SourceStringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlantUmlArtifactGeneratorShould {
    private static final String PLANT_UML_SOURCE = "some plantuml source code";

    @Mock
    private Chain chain;

    @Mock
    private Artifact artifact;

    @Mock
    private PlantUmlSourceBuilder plantUmlSourceBuilder;

    @Mock
    private SourceStringReader plantUmlSourceStringReader;

    @Mock
    private ComponentDiagramGenerator componentDiagramGenerator;

    private ByteArrayOutputStream imageStream = new ByteArrayOutputStream();


    @BeforeEach
    void setUp() {
        when(plantUmlSourceBuilder.build(chain)).thenReturn(PLANT_UML_SOURCE);
        when(componentDiagramGenerator.generate(PLANT_UML_SOURCE)).thenReturn(imageStream);
    }

    @Test
    void callPlantUmlWithSourceCode() {
        PlantUmlArtifactGenerator artifactGenerator = new PlantUmlArtifactGenerator(plantUmlSourceBuilder, componentDiagramGenerator);

        Artifact artifact = artifactGenerator.generate(chain);

        assertEquals(imageStream, artifact.document);
    }
}