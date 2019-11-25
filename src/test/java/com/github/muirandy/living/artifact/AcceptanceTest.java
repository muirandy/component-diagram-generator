package com.github.muirandy.living.artifact;

import com.github.muirandy.living.artifact.diagram.domain.*;
import com.github.muirandy.living.artifact.gateway.plantuml.ComponentDiagramGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlArtifactGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
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
    private HashMap<String, String> prefix2Uri;
    private String svg;

    private int elementIndex = 1;
    private int pathElementIndex = 1;
    private int textElementIndex = 1;
    private int imageElementIndex = 1;

    @BeforeEach
    void buildSvgNamespaceContext() {
        prefix2Uri = new HashMap<>();
        prefix2Uri.put("svg", "http://www.w3.org/2000/svg");
    }

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

    private void givenAnChainWith(Link... links) {
        chain = new Chain();
        for (Link link : links)
            chain.add(link);
    }

    private void thenDiagramContains(Link... links) {
        svg = getResultingSvg();
        XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).hasXPath("//svg:svg");

        for (Link link : links)
            assertLinkIsRendered(link);
    }

    private void assertLinkIsRendered(Link link) {
        drawsElement(link);
        if (link.hasConnections())
            drawsConnections(elementIndex, link);
    }

    private void drawsElement(Link link) {
        if (link instanceof QueueLink) {
            XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).nodesByXPath("//svg:svg/svg:g/svg:path[" + pathElementIndex++ + "]").exist();
            XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).nodesByXPath("//svg:svg/svg:g/svg:path[" + pathElementIndex++ + "]").exist();
            if (link instanceof ActiveMqQueueLink)
                XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).nodesByXPath("//svg:svg/svg:g/svg:image[" + imageElementIndex++ + "]").exist();
            else
                XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).valueByXPath("//svg:svg/svg:g/svg:text[" + textElementIndex++ + "]").isEqualTo(link.name);
        } else {
            XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).valueByXPath("//svg:svg/svg:g/svg:rect[" + elementIndex++ + "]").isEmpty();
            XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).valueByXPath("//svg:svg/svg:g/svg:text[" + textElementIndex++ + "]").isEqualTo(link.name);
        }
    }

    private void drawsConnections(int index, Link link) {
        for (Connection connection : link.connections) {
            XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).nodesByXPath("//svg:svg/svg:g/svg:path/@id").exist();
            XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).valueByXPath("//svg:svg/svg:g/svg:path/@id").isEqualTo(link.name + "->" + connection.target.name);
        }

    }

    @Test
    void twoElementsShownInDiagram() {
        Link link = new Link("First");
        Link link2 = new Link("Second");

        givenAnChainWith(link, link2);
        whenWeRunTheApp();
        thenDiagramContains(link, link2);
    }

    @Test
    void firstElementDependsOnSecondElement() {
        Link link = new Link("First");
        Link link2 = new Link("Second");
        link.connect(createConnection(link2));

        givenAnChainWith(link, link2);
        whenWeRunTheApp();
        thenDiagramContains(link, link2);
    }

    private Connection createConnection(Link otherLink) {
        return new Connection(otherLink);
    }

    @Test
    void drawsQueues() {
        Link link = new QueueLink("MyQueue");
        givenAnChainWith(link);
        whenWeRunTheApp();
        thenDiagramContains(link);
    }

    @Test
    void drawsActiveMqQueues() {
        Link link = new ActiveMqQueueLink("MyAmqQueue");
        givenAnChainWith(link);
        whenWeRunTheApp();
        thenDiagramContains(link);
    }

}