package com.github.muirandy.living.artifact;

import com.github.muirandy.living.artifact.diagram.domain.*;
import com.github.muirandy.living.artifact.gateway.plantuml.ComponentDiagramGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlArtifactGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlSourceBuilder;
import org.junit.jupiter.api.Test;
import org.xmlunit.assertj.XmlAssert;
import org.xmlunit.builder.Input;

import javax.xml.transform.Source;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;


class AcceptanceTest {

    PlantUmlSourceBuilder plantUmlSourceBuilder = new PlantUmlSourceBuilder();
    private Chain chain;
    private Artifact artifact;
    private ComponentDiagramGenerator componentDiagramGenerator = new ComponentDiagramGenerator();
    private ArtifactGenerator plantUmlArtifactGenerator = new PlantUmlArtifactGenerator(plantUmlSourceBuilder, componentDiagramGenerator);

    @Test
    void emptyChainGeneratesEmptyPlantUmlDiagram() {
        givenAnEmptyChain();
        whenWeRunTheApp();
        thenWeGetEmptyPlantUmlDiagramBack();
    }

    private void givenAnEmptyChain() {
        chain = new Chain();
    }

    private void whenWeRunTheApp() {
        App app = new App(plantUmlArtifactGenerator);
        artifact = app.run(chain);
    }


    private void thenWeGetEmptyPlantUmlDiagramBack() {
        File emptyImage = new File(AcceptanceTest.class.getClassLoader().getResource("empty.svg").getFile());
        Source expected = Input.fromFile(emptyImage).build();

        String svg = getResultingSvg();

        XmlAssert.assertThat(svg).and(expected)
                 .ignoreWhitespace()
                 .ignoreComments()
                 .areIdentical();
    }

    private String getResultingSvg() {
        return new String(artifact.document.toByteArray(), Charset.forName("UTF-8"));
    }

    @Test
    void singleElementShownInDiagram() {
        Link link = new Link("SingleItem");
        givenAnChainWith(link);
        whenWeRunTheApp();
        thenDiagramContains(link);
    }

    private void givenAnChainWith(Link link) {
        chain = new Chain();
        chain.add(link);
    }

    private void thenDiagramContains(Link... links) {
        HashMap<String, String> prefix2Uri = new HashMap<>();
        prefix2Uri.put("svg", "http://www.w3.org/2000/svg");

        String svg = getResultingSvg();
        XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).hasXPath("//svg:svg");

        XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).valueByXPath("//svg:svg/svg:g/svg:rect[1]").isEmpty();
        XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).valueByXPath("//svg:svg/svg:g/svg:text[1]").isEqualTo("SingleItem");
    }
}