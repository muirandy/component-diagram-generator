package com.github.muirandy.living.artifact.api.diagram;

import java.io.ByteArrayOutputStream;

public class Artifact {
    public ByteArrayOutputStream document;

    public Artifact(ByteArrayOutputStream diagramOutputStream) {
        document = diagramOutputStream;
    }
}
