package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.*;

import java.util.Set;
import java.util.stream.Collectors;

public class PlantUmlSourceBuilder {
    private static final String START_TAG = "@startuml\n";
    private static final String EMPTY_DOC_TAG = "skinparam monochrome false\n";
    private static final String END_TAG = "@enduml\n";

    private static final String NO_CONNECTIONS_TAG = "";
    private static final String NO_IMPORTS_TAG = "";

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
                + imports(chain)
                + createElementTags(chain)
                + createConnectionTags(chain)
                + END_TAG;
    }

    private String imports(Chain chain) {
        Set<String> elementTypeNames = chain.getLinks().stream()
                                   .map(l -> l.getClass().getSimpleName())
                                   .collect(Collectors.toSet());

        if (elementTypeNames.contains("ActiveMqQueueLink"))
            return "!include <cloudinsight/activemq>\n";

        return NO_IMPORTS_TAG;

        //             .filter(l -> l instanceof ActiveMqQueueLink)
//             .map(l -> "!include <cloudinsight/activemq>\n")
//             .distinct()
//                    .findAny()
//                    .ifPresent()
//                ;

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
        if (link instanceof ActiveMqQueueLink)
            return "queue \"<$activemq>\" as " + link.name + " #Crimson\n";
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
