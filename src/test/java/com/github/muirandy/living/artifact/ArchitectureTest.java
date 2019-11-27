package com.github.muirandy.living.artifact;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.github.muirandy")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()

            .layer("PlantUmlGateway").definedBy("com.github.muirandy.living.artifact.gateway.plantuml")
            .layer("PlantUml").definedBy("net.sourceforge.plantuml")
            .layer("JaegerTracingGateway").definedBy("com.github.muirandy.living.artifact.gateway.jaeger")
            .layer("JaegerTracingApi").definedBy("com.github.muirandy.living.artifact.api.chain")
            .layer("Domain").definedBy("com.github.muirandy.living.artifact.diagram.domain")
            .layer("Main").definedBy("com.github.muirandy.living.artifact")

            .whereLayer("JaegerTracingGateway").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("JaegerTracingApi").mayOnlyBeAccessedByLayers("JaegerTracingGateway", "Main")
            .whereLayer("PlantUmlGateway").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("PlantUml").mayOnlyBeAccessedByLayers("PlantUmlGateway")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("PlantUmlGateway", "JaegerTracingGateway", "Main");
}
