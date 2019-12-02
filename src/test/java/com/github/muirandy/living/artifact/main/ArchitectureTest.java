package com.github.muirandy.living.artifact.main;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = {"com.github.muirandy", "kong.unirest.json", "net.sourceforge.plantuml"})
public class ArchitectureTest {

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()

            .layer("RestClient").definedBy("kong.unirest..")
            .layer("PlantUmlGateway").definedBy("com.github.muirandy.living.artifact.gateway.plantuml")
            .layer("PlantUml").definedBy("net.sourceforge.plantuml..")
            .layer("JaegerTracingGateway").definedBy("com.github.muirandy.living.artifact.gateway.jaeger")
            .layer("TracingApi").definedBy("com.github.muirandy.living.artifact.api.chain")
            .layer("Domain").definedBy("com.github.muirandy.living.artifact.diagram.domain")
            .layer("Main").definedBy("com.github.muirandy.living.artifact.main")

            .whereLayer("JaegerTracingGateway").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("TracingApi").mayOnlyBeAccessedByLayers("JaegerTracingGateway", "Main")
            .whereLayer("RestClient").mayOnlyBeAccessedByLayers("JaegerTracingGateway", "Main", "RestClient")
            .whereLayer("PlantUmlGateway").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("PlantUml").mayOnlyBeAccessedByLayers("PlantUmlGateway", "Main", "PlantUml")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("TracingApi", "PlantUmlGateway", "JaegerTracingGateway", "Main");


    @Test
    void jaegerNaming() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.github.muirandy.living.artifact");
        ArchRule jaegerRule = classes().that().haveSimpleNameStartingWith("Jaeger")
                .should().resideInAPackage("com.github.muirandy.living.artifact.gateway.jaeger");
        jaegerRule.check(importedClasses);
    }

    @Test
    void plantUmlNaming() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("com.github.muirandy.living.artifact");
        ArchRule jaegerRule = classes().that().haveSimpleNameStartingWith("PlantUml")
                .should().resideInAPackage("com.github.muirandy.living.artifact.gateway.plantuml");
        jaegerRule.check(importedClasses);
    }
}
