package com.github.muirandy.gateway.plantuml;

import com.github.muirandy.diagram.domain.Chain;

public class PlantUmlSourceBuilder {
    private static final String START_TAG = "@startuml\n";
    private static final String EMPTY_DOC_TAG = "skinparam monochrome false\n";
    private static final String END_TAG = "@enduml\n";

    public String build(Chain chain) {
        return buildEmptyDiagram();
    }

    private String buildEmptyDiagram() {
        return START_TAG + EMPTY_DOC_TAG + END_TAG;
    }
}
