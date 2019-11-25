package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.diagram.domain.Link;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlantUmlSourceBuilderShould {
    private static final String START_TAG = "@startuml\n";
    private static final String EMPTY_DOC_TAG = "skinparam monochrome false\n";
    private static final String RECTANGLE_TAG = "rectangle SingleItem\n";
    private static final String END_TAG = "@enduml\n";

    private static final String LINK_NAME = "SingleItem";

    private Chain chain;

    @Test
    void buildDocumentForEmptyChain() {
        PlantUmlSourceBuilder sourceBuilder = new PlantUmlSourceBuilder();
        chain = new Chain();

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(START_TAG, EMPTY_DOC_TAG, END_TAG);
    }

    @Test
    void buildForSingleLink() {
        PlantUmlSourceBuilder sourceBuilder = new PlantUmlSourceBuilder();
        chain = new Chain();
        Link singleLink = new Link(LINK_NAME);
        chain.add(singleLink);

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(
                START_TAG,
                RECTANGLE_TAG,
                END_TAG);
    }
}