package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.*;

public class PlantUmlSourceStringVisitor implements SourceStringVisitor {
    @Override
    public String visit(QueueLink link) {
        return "queue " + getLinkName(link) + "\n";
    }

    @Override
    public String visit(ActiveMqQueueLink link) {
        return "queue \"<$activemq>\" as " + getLinkName(link) + " #Crimson\n";
    }

    @Override
    public String visit(RectangleLink link) {
        return "rectangle " + getLinkName(link) + "\n";
    }

    @Override
    public String visit(KsqlLink link) {
        return "rectangle " + getKsqlLinkName(link) + "\n";
    }

    private String getKsqlLinkName(KsqlLink link) {
        String trimmed = removeProducerPostfix(removeConsumerPostfix(link.name));
        return replaceHypenWithNonBreakingHyphen(trimmed);
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
