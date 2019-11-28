package com.github.muirandy.living.artifact.diagram.domain;

import com.github.muirandy.living.artifact.gateway.jaeger.JaegerChainBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppShould {
    private static final String TRACE_ID = "0123456789abcdef";

    @Mock
    private Chain chain;
    @Mock
    private Artifact artifact;
    @Mock
    private ArtifactGenerator artifactGenerator;
    @Mock
    private JaegerChainBuilder jaegerChainBuilder;

    @Test
    void callArtifactGenerator() {
        App app = new App(jaegerChainBuilder, artifactGenerator);
        when(artifactGenerator.generate(chain)).thenReturn(artifact);
        when(jaegerChainBuilder.build(TRACE_ID)).thenReturn(chain);

        Artifact result = app.run(TRACE_ID);

        assertThat(result).isEqualTo(artifact);
    }
}