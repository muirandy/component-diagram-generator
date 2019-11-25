package com.github.muirandy.living.artifact.gateway.plantuml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComponentDiagramGeneratorShould {
    private static final String PLANT_UML_SOURCE = "Plant UML Source Code";

    @Spy
    private ComponentDiagramGenerator componentDiagramGenerator;

    @Mock
    private SourceStringReader sourceStringReader;

    @BeforeEach
    void setUp() {
        doReturn(sourceStringReader).when(componentDiagramGenerator).createSourceStringReader(PLANT_UML_SOURCE);
    }

    @Test
    void callPlantUmlToGenerateDiagramFromSourceCode() throws IOException {
        ByteArrayOutputStream generate = componentDiagramGenerator.generate(PLANT_UML_SOURCE);

        verify(sourceStringReader).outputImage(eq(generate), any(FileFormatOption.class));
    }

    @Test
    void callPlantUmlToGenerateSvgOutput() throws IOException {
        ArgumentCaptor<FileFormatOption> svgFileFormatArgument = ArgumentCaptor.forClass(FileFormatOption.class);

        componentDiagramGenerator.generate(PLANT_UML_SOURCE);

        verify(sourceStringReader).outputImage(any(ByteArrayOutputStream.class), svgFileFormatArgument.capture());
        assertThat(svgFileFormatArgument.getValue().getFileFormat()).isEqualTo(FileFormat.SVG);
    }

    @Test
    void outputStreamGetsClosed() throws IOException {
        ByteArrayOutputStream outputStream = mock(ByteArrayOutputStream.class);
        when(componentDiagramGenerator.createNewOutputStream()).thenReturn(outputStream);

        componentDiagramGenerator.generate(PLANT_UML_SOURCE);

        verify(outputStream).close();
    }
}