package com.github.muirandy.living.artifact.main;

import com.github.muirandy.living.artifact.api.chain.ChainBuilder;
import com.github.muirandy.living.artifact.diagram.domain.Artifact;
import com.github.muirandy.living.artifact.diagram.domain.ArtifactGenerator;
import com.github.muirandy.living.artifact.diagram.domain.Chain;
import com.github.muirandy.living.artifact.main.App;
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

    @Test
    void callArtifactGenerator() {
        App app = new App(chainBuilder, artifactGenerator);
        when(artifactGenerator.generate(chain)).thenReturn(artifact);
        when(chainBuilder.build(TRACE_ID)).thenReturn(chain);

        Artifact result = app.run(TRACE_ID);

        assertThat(result).isEqualTo(artifact);
    }
}