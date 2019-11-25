package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.diagram.domain.Connection;
import com.github.muirandy.living.artifact.diagram.domain.Link;
import com.github.muirandy.living.artifact.diagram.domain.QueueLink;

import java.util.stream.Collectors;

public class PlantUmlSourceBuilder {
    private static final String START_TAG = "@startuml\n";
    private static final String EMPTY_DOC_TAG = "skinparam monochrome false\n";
    private static final String END_TAG = "@enduml\n";
    private static final String NO_CONNECTIONS_TAG = "";

    public String build(Chain chain) {
        if (chain.isEmpty())
            return buildEmptyDiagram();
        return buildDiagram(chain);
    }

    private String buildEmptyDiagram() {
        return START_TAG + EMPTY_DOC_TAG + END_TAG;
    }

    private String buildDiagram(Chain chain) {
        return START_TAG
                + createElementTags(chain)
                + createConnectionTags(chain)
                + END_TAG;
    }

    private String createElementTags(Chain chain) {
        return chain.getLinks().stream()
                    .map(l -> createLinkTag(l))
                    .collect(Collectors.joining());
    }

    private String createConnectionTags(Chain chain) {
        return chain.getLinks().stream()
                    .map(l -> createConnectionTags(l))
                    .collect(Collectors.joining());
    }

    private String createLinkTag(Link link) {
        if (link instanceof QueueLink)
            return "queue " + link.name + "\n";
        return "rectangle " + link.name + "\n";
    }

    private String createConnectionTags(Link link) {
        if (link.hasConnections())
            return link.connections.stream()
                                   .map(c -> createConnectionTag(link, c))
                                   .collect(Collectors.joining());
        return NO_CONNECTIONS_TAG;
    }

    private String createConnectionTag(Link link, Connection c) {
        return link.name + "->" + c.target.name + "\n";
    }

}
