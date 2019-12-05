package com.github.muirandy.living.artifact.gateway.plantuml;

import com.github.muirandy.living.artifact.api.diagram.*;

public class PlantUmlSourceImportVisitor implements SourceStringVisitor {

    private static final String NO_IMPORT_TAG = "";

    @Override
    public String visit(QueueLink link) {
        return NO_IMPORT_TAG;
    }

    @Override
    public String visit(ActiveMqQueueLink link) {
        return "!include <cloudinsight/activemq>\n";
    }

    @Override
    public String visit(RectangleLink link) {
        return NO_IMPORT_TAG;
    }

    @Override
    public String visit(KsqlLink ksqlLink) {
        return "!include customSprites/ksql.puml\n";
    }
}
