package com.github.muirandy.living.artifact.domain;

import com.github.muirandy.living.artifact.api.diagram.Artifact;
import com.github.muirandy.living.artifact.api.diagram.ArtifactGenerator;
import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;
import com.github.muirandy.living.artifact.api.trace.OpenTracingClient;
import com.github.muirandy.living.artifact.api.trace.Trace;
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
    private ChainBuilder chainBuilder;

    @Mock
    private OpenTracingClient openTracingClient;

    @Mock
    private ChainDecorator chainDecorator;

    @Mock
    private Trace trace;

    @Mock
    private Chain decoratedChain;


    @Test
    void callArtifactGenerator() {
        App app = new App(openTracingClient, chainBuilder, artifactGenerator);
        when(openTracingClient.obtainTrace(TRACE_ID)).thenReturn(trace);
        when(chainBuilder.build(trace)).thenReturn(chain);
        when(artifactGenerator.generate(chain)).thenReturn(artifact);

        Artifact result = app.obtainArtifact(TRACE_ID);

        assertThat(result).isEqualTo(artifact);
    }

    @Test
    void enhanceWithDecoratedContent() {
        App app = new App(openTracingClient, chainBuilder, chainDecorator, artifactGenerator);
        when(openTracingClient.obtainTrace(TRACE_ID)).thenReturn(trace);
        when(chainBuilder.build(trace)).thenReturn(chain);
        when(chainDecorator.decorate(chain)).thenReturn(decoratedChain);
        when(artifactGenerator.generate(decoratedChain)).thenReturn(artifact);

        Artifact result = app.obtainArtifact(TRACE_ID);

        assertThat(result).isEqualTo(artifact);
    }
}