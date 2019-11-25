package com.github.muirandy.gateway.plantuml;

import com.github.muirandy.diagram.domain.Chain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlantUmlSourceBuilderShould {
    private static final String START_TAG = "@startuml\n";
    private static final String EMPTY_DOC_TAG = "skinparam monochrome false\n";
    private static final String END_TAG = "@enduml\n";

    private Chain chain;

    @Test
    void buildDocumentForEmptyChain() {
        PlantUmlSourceBuilder sourceBuilder = new PlantUmlSourceBuilder();
        chain = new Chain();

        String plantUmlSourceCode = sourceBuilder.build(chain);

        assertThat(plantUmlSourceCode).containsSequence(START_TAG, EMPTY_DOC_TAG, END_TAG);
    }
}