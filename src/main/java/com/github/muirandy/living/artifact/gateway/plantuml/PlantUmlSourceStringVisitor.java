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

    private String getLinkName(Link link) {
        return replaceHypenWithNonBreakingHyphen(link);
    }

    private String replaceHypenWithNonBreakingHyphen(Link link) {
        return link.name.replaceAll("-", "_");
    }
}
