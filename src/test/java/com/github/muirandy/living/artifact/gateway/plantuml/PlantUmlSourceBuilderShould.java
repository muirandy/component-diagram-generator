package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlantUmlSourceBuilderShould {
    private static final String START_TAG = "@startuml\n";
    private static final String EMPTY_DOC_TAG = "skinparam monochrome false\n";
    private static final String END_TAG = "@enduml\n";

    private static final String FIRST_ELEMENT_NAME = "SingleItem";
    private static final String RECTANGLE_TAG = "rectangle " + FIRST_ELEMENT_NAME + "\n";
    private static final String RECTANGLE_SPECIAL_CHAR_TAG = "rectangle " + "Single" + "_" + "Item" + "\n";

    private static final String SECOND_ELEMENT_NAME = "SecondLink";
    private static final String SECOND_RECTANGLE_TAG = "rectangle " + SECOND_ELEMENT_NAME + "\n";
    private static final String SECOND_RECTANGLE_SPECIAL_CHAR_TAG = "rectangle " + "Second" + "_" + "Item" + "\n";

    private static final String LINK_NAME = FIRST_ELEMENT_NAME;
    private static final String CONNECTION_FROM_RECTANGLE_TO_SECOND_RECTANGLE_TAG = FIRST_ELEMENT_NAME + "->" + SECOND_ELEMENT_NAME + "\n";

    private static final String QUEUE_TAG = "queue " + FIRST_ELEMENT_NAME + "\n";

    private static final String ACTIVE_MQ_SPRITE_IMPORT = "!include <cloudinsight/activemq>\n";
    private static final String ACTIVE_MQ_QUEUE_TAG = "queue \"<$activemq>\" as " + FIRST_ELEMENT_NAME + " #Crimson\n";

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
        createChain(new RectangleLink(LINK_NAME));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_TAG,
                END_TAG);
    }

    @Test
    void buildForSingleLinkWithSpecialCharacterAsHyphenIsNotAllowableInPlantUmlSource() {
        createChain(new RectangleLink("Single-Item"));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_SPECIAL_CHAR_TAG,
                END_TAG);
    }

    @Test
    void buildMultipleLinks() {
        createChain(new RectangleLink(LINK_NAME), new RectangleLink(SECOND_ELEMENT_NAME));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_TAG,
                SECOND_RECTANGLE_TAG,
                END_TAG);
    }

    @Test
    void connectLinksUsingConnectors() {
        Link firstLink = new RectangleLink(LINK_NAME);
        Link secondLink = new RectangleLink(SECOND_ELEMENT_NAME);
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
    void connectLinksWithSpecialCharactersUsingConnectors() {
        Link firstLink = new RectangleLink("Single-Item");
        Link secondLink = new RectangleLink("Second-Item");
        firstLink.connect(new Connection(secondLink));
        createChain(firstLink, secondLink);

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_SPECIAL_CHAR_TAG,
                SECOND_RECTANGLE_SPECIAL_CHAR_TAG,
                "Single_Item->Second_Item\n",
                END_TAG);
    }

    @Test
    void mergeProducerAndConsumerLinks() {
        createChain(new RectangleLink("link-producer"), new RectangleLink("link-consumer"));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                "rectangle " + "link" + "\n",
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

    @Test
    void buildActiveMqQueue() {
        createChain(new ActiveMqQueueLink(LINK_NAME));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                ACTIVE_MQ_SPRITE_IMPORT,
                ACTIVE_MQ_QUEUE_TAG,
                END_TAG);
    }

    @Test
    void buildKsql() {
        createChain(new KsqlLink(LINK_NAME));

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_TAG,
                END_TAG);
    }

    private void createChain(Link... links) {
        chain = new Chain();
        for (Link link : links)
            chain.add(link);
    }
}