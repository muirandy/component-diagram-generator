package com.github.muirandy.gateway.plantuml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ComponentDiagramGenerator {

    public ByteArrayOutputStream generate(String plantUmlSource) {
        SourceStringReader sourceStringReader = createSourceStringReader(plantUmlSource);
        ByteArrayOutputStream byteArrayOutputStream = createNewOutputStream();
        try {
            sourceStringReader.outputImage(byteArrayOutputStream, new FileFormatOption(FileFormat.SVG));
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        SourceStringReader reader = new SourceStringReader(source);
final ByteArrayOutputStream os = new ByteArrayOutputStream();
// Write the first image to "os"
String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
os.close();
         */
        return byteArrayOutputStream;
    }

    protected SourceStringReader createSourceStringReader(String plantUmlSource) {
        return new SourceStringReader(plantUmlSource);
    }

    protected ByteArrayOutputStream createNewOutputStream() {
        return new ByteArrayOutputStream();
    }
}
