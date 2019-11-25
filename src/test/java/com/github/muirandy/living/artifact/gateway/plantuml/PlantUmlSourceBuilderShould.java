package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.diagram.domain.Connection;
import com.github.muirandy.living.artifact.diagram.domain.Link;
import com.github.muirandy.living.artifact.diagram.domain.QueueLink;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlantUmlSourceBuilderShould {
    private static final String START_TAG = "@startuml\n";
    private static final String EMPTY_DOC_TAG = "skinparam monochrome false\n";
    private static final String END_TAG = "@enduml\n";

    private static final String FIRST_ELEMENT_NAME = "SingleItem";
    private static final String RECTANGLE_TAG = "rectangle " + FIRST_ELEMENT_NAME + "\n";

    private static final String SECOND_ELEMENT_NAME = "SecondLink";
    private static final String SECOND_RECTANGLE_TAG = "rectangle " + SECOND_ELEMENT_NAME + "\n";

    private static final String LINK_NAME = FIRST_ELEMENT_NAME;
    private static final String CONNECTION_FROM_RECTANGLE_TO_SECOND_RECTANGLE_TAG = FIRST_ELEMENT_NAME + "->" + SECOND_ELEMENT_NAME + "\n";

    private static final String QUEUE_TAG = "queue " + FIRST_ELEMENT_NAME + "\n";

    private Chain chain;
    private final PlantUmlSourceBuilder sourceBuilder = new PlantUmlSourceBuilder();

    @Test
    void buildDocumentForEmptyChain() {
        chain = new Chain();

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(START_TAG, EMPTY_DOC_TAG, END_TAG);
    }

    @Test
    void buildForSingleLink() {
        createChain(new Link(LINK_NAME));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_TAG,
                END_TAG);
    }

    @Test
    void buildMultipleLinks() {
        createChain(new Link(LINK_NAME), new Link(SECOND_ELEMENT_NAME));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_TAG,
                SECOND_RECTANGLE_TAG,
                END_TAG);
    }

    @Test
    void connectLinksUsingConnectors() {
        Link firstLink = new Link(LINK_NAME);
        Link secondLink = new Link(SECOND_ELEMENT_NAME);
        firstLink.connect(new Connection(secondLink));
        createChain(firstLink, secondLink);

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_TAG,
                SECOND_RECTANGLE_TAG,
                CONNECTION_FROM_RECTANGLE_TO_SECOND_RECTANGLE_TAG,
                END_TAG);
    }

    @Test
    void buildQueue() {
        createChain(new QueueLink(LINK_NAME));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                QUEUE_TAG,
                END_TAG);
    }

    private void createChain(Link... links) {
        chain = new Chain();
        for (Link link : links)
            chain.add(link);
    }
}