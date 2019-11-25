package com.github.muirandy;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.github.muirandy")
public class ArchitectureTest {

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()

            .layer("PlantUmlGateway").definedBy("com.github.muirandy.gateway.plantuml")
            .layer("Domain").definedBy("com.github.muirandy.diagram.domain")
            .layer("Main").definedBy("com.github.muirandy")

            .whereLayer("PlantUmlGateway").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("PlantUmlGateway", "Main");
}
