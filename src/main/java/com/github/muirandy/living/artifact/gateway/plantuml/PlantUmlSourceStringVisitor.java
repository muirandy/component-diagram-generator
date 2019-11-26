package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.diagram.domain.*;

public class PlantUmlSourceStringVisitor implements SourceStringVisitor {
    @Override
    public String visit(QueueLink link) {
        return "queue " + link.name + "\n";
    }

    @Override
    public String visit(ActiveMqQueueLink link) {
        return "queue \"<$activemq>\" as " + link.name + " #Crimson\n";
    }

    @Override
    public String visit(RectangleLink link) {
        return "rectangle " + link.name + "\n";
    }
}
