package com.github.muirandy.diagram.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppShould {
    @Mock
    private Chain chain;
    @Mock
    private Artifact artifact;
    @Mock
    private ArtifactGenerator artifactGenerator;

    @Test
    void callArtifactGenerator() {
        App app = new App(artifactGenerator);
        when(artifactGenerator.generate(chain)).thenReturn(artifact);

        Artifact result = app.run(chain);

        assertThat(result).isEqualTo(artifact);
    }
}