package com.github.muirandy.diagram.domain;

import java.io.ByteArrayOutputStream;

public class Artifact {
    public ByteArrayOutputStream document;

    public Artifact(ByteArrayOutputStream diagramOutputStream) {
        document = diagramOutputStream;
    }
}
