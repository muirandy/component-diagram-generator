package com.github.muirandy;

import com.github.muirandy.diagram.domain.App;
import com.github.muirandy.diagram.domain.Artifact;
import com.github.muirandy.diagram.domain.Chain;
import org.junit.jupiter.api.Test;

public class AcceptanceTest {

    private Chain chain;
    private Artifact artifact;

    @Test
    void t() {
        givenAnEmptyChain();
        whenWeRunTheApp();
        thenWeGetPlantUmlDiagramBack();
    }

    private void givenAnEmptyChain() {
        chain = new Chain();
    }

    private void whenWeRunTheApp() {
        App app = new App();
        artifact = app.run(chain);
    }

    private void thenWeGetPlantUmlDiagramBack() {

    }

}