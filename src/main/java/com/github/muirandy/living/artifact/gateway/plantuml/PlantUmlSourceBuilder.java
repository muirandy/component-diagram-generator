package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.diagram.Connection;
import com.github.muirandy.living.artifact.api.diagram.Link;

import java.util.Set;
import java.util.stream.Collectors;

import static com.github.muirandy.living.artifact.api.diagram.LinkRelationship.PRODUCER;

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
    }

    private String createElementTags(Chain chain) {
        return chain.getLinks().stream()
                .map(l -> createLinkTag(l))
                .distinct()
                .collect(Collectors.joining());
    }

    private String createConnectionTags(Chain chain) {
        return chain.getLinks().stream()
                .map(l -> createConnectionTags(l))
                .collect(Collectors.joining());
    }

    private String createLinkTag(Link link) {
        return link.toSourceString(new PlantUmlSourceStringVisitor());
    }

    private String createConnectionTags(Link link) {
        if (link.hasConnections())
            return link.connections.stream()
                    .map(c -> createConnectionTag(link, c))
                    .collect(Collectors.joining());
        return NO_CONNECTIONS_TAG;
    }

    private String createConnectionTag(Link link, Connection c) {
        if (PRODUCER.equals(c.relationship))
            return getLinkName(link) + "->" + getLinkName(c.target) + "\n";
        return getLinkName(c.target) + "<-" + getLinkName(link) + "\n";
    }

    private String getLinkName(Link link) {
        String trimmed = removeProducerPostfix(removeConsumerPostfix(link.name));
        return replaceHypenWithNonBreakingHyphen(trimmed);
    }

    private String replaceHypenWithNonBreakingHyphen(String name) {
        return name.replaceAll("-", "_");
    }

    private String removeProducerPostfix(String name) {
        if (name.endsWith("-producer"))
            return name.substring(0, name.length() - "-producer".length());
        return name;
    }

    private String removeConsumerPostfix(String name) {
        if (name.endsWith("-consumer"))
            return name.substring(0, name.length() - "-consumer".length());
        return name;
    }
}
