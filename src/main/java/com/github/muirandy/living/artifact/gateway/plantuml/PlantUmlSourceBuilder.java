package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.diagram.domain.Link;

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
        Link link = chain.getLinks().get(0);
        String linkTag = createLinkTag(link);
        return START_TAG + linkTag + END_TAG;
    }

    private String createLinkTag(Link link) {
        return "rectangle " + link.name + "\n";
    }

    private String buildEmptyDiagram() {
        return START_TAG + EMPTY_DOC_TAG + END_TAG;
    }
}
