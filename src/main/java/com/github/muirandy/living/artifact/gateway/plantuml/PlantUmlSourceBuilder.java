package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.diagram.domain.Link;

import java.util.stream.Collectors;

public class PlantUmlSourceBuilder {
    private static final String START_TAG = "@startuml\n";
    private static final String EMPTY_DOC_TAG = "skinparam monochrome false\n";
    private static final String END_TAG = "@enduml\n";

    public String build(Chain chain) {
        if (chain.isEmpty())
            return buildEmptyDiagram();
        return buildDiagram(chain);
    }

    private String buildDiagram(Chain chain) {
        String tags = chain.getLinks().stream()
                              .map(l -> createLinkTag(l))
                              .collect(Collectors.joining());
        return START_TAG + tags + END_TAG;
    }

    private String createLinkTag(Link link) {
        return "rectangle " + link.name + "\n";
    }

    private String buildEmptyDiagram() {
        return START_TAG + EMPTY_DOC_TAG + END_TAG;
    }
}
