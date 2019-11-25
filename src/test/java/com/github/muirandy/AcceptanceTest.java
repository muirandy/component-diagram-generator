package com.github.muirandy;

import com.github.muirandy.diagram.domain.App;
import com.github.muirandy.diagram.domain.Artifact;
import com.github.muirandy.diagram.domain.ArtifactGenerator;
import com.github.muirandy.diagram.domain.Chain;
import com.github.muirandy.gateway.plantuml.ComponentDiagramGenerator;
import com.github.muirandy.gateway.plantuml.PlantUmlArtifactGenerator;
import com.github.muirandy.gateway.plantuml.PlantUmlSourceBuilder;
import org.junit.jupiter.api.Test;
import org.xmlunit.assertj.XmlAssert;
import org.xmlunit.builder.Input;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.nio.charset.Charset;

class AcceptanceTest {

    private Chain chain;
    private Artifact artifact;
    PlantUmlSourceBuilder plantUmlSourceBuilder = new PlantUmlSourceBuilder();
    private ComponentDiagramGenerator componentDiagramGenerator = new ComponentDiagramGenerator();
    private ArtifactGenerator plantUmlArtifactGenerator = new PlantUmlArtifactGenerator(plantUmlSourceBuilder, componentDiagramGenerator);

    @Test
    void emptyChainGeneratesEmptyPlantUmlDiagram() {
        givenAnEmptyChain();
        whenWeRunTheApp();
        thenWeGetPlantUmlDiagramBack();
    }

    private void givenAnEmptyChain() {
        chain = new Chain();
    }

    private void whenWeRunTheApp() {
        App app = new App(plantUmlArtifactGenerator);
        artifact = app.run(chain);
    }

    private void thenWeGetPlantUmlDiagramBack() {
        File emptyImage = new File(AcceptanceTest.class.getClassLoader().getResource("empty.svg").getFile());
        Source expected = Input.fromFile(emptyImage).build();

        String svg = new String(artifact.document.toByteArray(), Charset.forName("UTF-8"));

        XmlAssert.assertThat(svg).and(expected)
                 .ignoreWhitespace()
                 .ignoreComments()
                 .areIdentical();
    }
}