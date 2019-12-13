package com.github.muirandy.living.artifact.main;

import com.github.muirandy.living.artifact.api.diagram.Artifact;
import com.github.muirandy.living.artifact.api.diagram.ArtifactGenerator;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;
import com.github.muirandy.living.artifact.api.trace.OpenTracingClient;
import com.github.muirandy.living.artifact.domain.App;
import com.github.muirandy.living.artifact.domain.ChainBuilder;
import com.github.muirandy.living.artifact.gateway.jaeger.JaegerClient;
import com.github.muirandy.living.artifact.gateway.kafka.KafkaChainDecorator;
import com.github.muirandy.living.artifact.gateway.kafka.KafkaHeader;
import com.github.muirandy.living.artifact.gateway.kafka.KafkaTopicConsumer;
import com.github.muirandy.living.artifact.gateway.plantuml.ComponentDiagramGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlArtifactGenerator;
import com.github.muirandy.living.artifact.gateway.plantuml.PlantUmlSourceBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ComponentDiagramApp {
    private static final String B3_TRACE_NAME = "X-B3-TraceId";
    private static final String KAFKA_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
    private static String jaegerServer;
    private static String kafkaBootstrapServers;
    private List<String> existingTraceIds;

    public ComponentDiagramApp(String[] args) {
        readArgs(args);
        existingTraceIds = new ArrayList<>();
    }

    private void readArgs(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("There must be 2 arguments: \n"
                    + "jaegerServer(eg: http://jaeger:16686)\n"
                    + "kafkaBootstrapServers(eg: broker:9092)\n");

        jaegerServer = args[0];
        kafkaBootstrapServers = args[1];
        System.out.println("Jaeger Server: " + jaegerServer);
        System.out.println("Kafka: " + kafkaBootstrapServers);
    }

    public static void main(String[] args) {
        ComponentDiagramApp componentDiagramApp = new ComponentDiagramApp(args);
    }

    public void initialise() {
        existingTraceIds = createJaegerClient().obtainTraceIds();
    }

    private JaegerClient createJaegerClient() {
        return new JaegerClient(jaegerServer);
    }

    public ByteArrayOutputStream drawComponentDiagram() {
        String traceId = getNewTraceId();

        App app = new App(createOpenTracingClient(),
                createChainBuilder(),
                createKafkaChainEnhancer(traceId),
                createArtifactGenerator());

        Artifact artifact = app.obtainArtifact(traceId);
        return artifact.document;
    }

    private String getNewTraceId() {
        List<String> allTraceIds = createJaegerClient().obtainTraceIds();
        allTraceIds.removeAll(existingTraceIds);
        return allTraceIds.get(0);
    }

    private OpenTracingClient createOpenTracingClient() {
        return createJaegerClient();
    }

    private ChainBuilder createChainBuilder() {
        return new ChainBuilder();
    }

    private ChainDecorator createKafkaChainEnhancer(String traceId) {
        KafkaHeader header = new KafkaHeader(B3_TRACE_NAME, traceId);
        KafkaTopicConsumer kafkaTopicConsumer = new KafkaTopicConsumer(kafkaProperties(), header);
        return new KafkaChainDecorator(kafkaTopicConsumer);
    }

    private ArtifactGenerator createArtifactGenerator() {
        ComponentDiagramGenerator componentDiagramGenerator = new ComponentDiagramGenerator();
        PlantUmlSourceBuilder plantUmlSourceBuilder = new PlantUmlSourceBuilder();
        return new PlantUmlArtifactGenerator(plantUmlSourceBuilder, componentDiagramGenerator);
    }

    private Properties kafkaProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put("acks", "all");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KAFKA_DESERIALIZER);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KAFKA_DESERIALIZER);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaChainDecorator");
        return props;
    }
}