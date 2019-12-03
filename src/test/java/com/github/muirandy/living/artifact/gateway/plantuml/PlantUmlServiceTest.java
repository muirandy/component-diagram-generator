package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.api.diagram.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlunit.assertj.XmlAssert;
import org.xmlunit.builder.Input;

import javax.xml.transform.Source;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

import static com.github.muirandy.living.artifact.api.diagram.LinkRelationship.CONSUMER;
import static com.github.muirandy.living.artifact.api.diagram.LinkRelationship.PRODUCER;

class PlantUmlServiceTest {

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

    @Test
    void singleElementShownInDiagram() {
        Link link = new RectangleLink("SingleItem");
        givenAnChainWith(link);
        whenWeRunTheApp();
        thenDiagramContains(link);
    }

    @Test
    void twoElementsShownInDiagram() {
        Link link = new RectangleLink("First");
        Link link2 = new RectangleLink("Second");

        givenAnChainWith(link, link2);
        whenWeRunTheApp();
        thenDiagramContains(link, link2);
    }

    @Test
    void firstElementDependsOnSecondElement() {
        Link link = new RectangleLink("First");
        Link link2 = new RectangleLink("Second");
        link.connect(createProducerConnection(link2));

        givenAnChainWith(link, link2);
        whenWeRunTheApp();
        thenDiagramContains(link, link2);
    }

    @Test
    void secondElementConsumesFromFirstElement() {
        Link topic = new RectangleLink("Topic");
        Link consumer = new RectangleLink("Consumer");
        consumer.connect(createConsumerConnection(topic));

        givenAnChainWith(topic, consumer);
        whenWeRunTheApp();
        thenDiagramContains(topic, consumer);
    }

    private Connection createConsumerConnection(Link otherLink) {
        return new Connection(CONSUMER, otherLink);
    }

    private Connection createProducerConnection(Link otherLink) {
        return new Connection(PRODUCER, otherLink);
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

    private void givenAnEmptyChain() {
        chain = new Chain();
    }

    private void whenWeRunTheApp() {
        artifact = plantUmlArtifactGenerator.generate(chain);
    }

    private void thenWeGetEmptyPlantUmlDiagramBack() {
        File emptyImage = new File(PlantUmlServiceTest.class.getClassLoader().getResource("empty.svg").getFile());
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
            drawsConnections(link);
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

    private void drawsConnections(Link link) {
        for (Connection connection : link.connections) {
            XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).nodesByXPath("//svg:svg/svg:g/svg:path/@id").exist();
            if (PRODUCER.equals(connection.relationship))
                XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).valueByXPath("//svg:svg/svg:g/svg:path/@id").isEqualTo(link.name + "->" + connection.target.name);
            else
                XmlAssert.assertThat(svg).withNamespaceContext(prefix2Uri).valueByXPath("//svg:svg/svg:g/svg:path/@id").isEqualTo(connection.target.name + "<-" + link.name);
        }
    }
}