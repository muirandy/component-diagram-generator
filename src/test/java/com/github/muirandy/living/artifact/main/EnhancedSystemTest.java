package com.github.muirandy.living.artifact.main;

import com.github.muirandy.living.artifact.api.diagram.Chain;
import com.github.muirandy.living.artifact.api.diagram.KafkaTopicLink;
import com.github.muirandy.living.artifact.api.diagram.Link;
import com.github.muirandy.living.artifact.api.enhancer.ChainDecorator;
import com.github.muirandy.living.artifact.gateway.kafka.KafkaChainDecorator;
import com.github.muirandy.living.artifact.gateway.kafka.KafkaTopicConsumer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class EnhancedSystemTest {
    private static final String TOPIC_NAME = "topic-1";
    private static final String KAFKA_DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
    private static final String KAFKA_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

    @Container
    protected static final KafkaContainer KAFKA_CONTAINER =
            new KafkaContainer("5.3.0").withEmbeddedZookeeper()
                                       .waitingFor(Wait.forLogMessage(".*Launching kafka.*\\n", 1))
                                       .waitingFor(Wait.forLogMessage(".*started.*\\n", 1));

    private static final String KEY = "Expected Key";
    private static final String MESSAGE = "Expected Value";
    private static final Integer NO_PARTITION = null;
    private static final String TRACING_HEADER = "OpenTracing Header";
    private static final String TRACE_ID = "Trace Id";
    private List<Header> headers = new ArrayList<>();
    private Chain decoratedChain;

    @BeforeEach
    void beforeAll() {
        createTopics();
    }

    void createTopics() {
        AdminClient adminClient = AdminClient.create(getKafkaProperties());

        CreateTopicsResult createTopicsResult = adminClient.createTopics(getTopics(), new CreateTopicsOptions().timeoutMs(1000));
        Map<String, KafkaFuture<Void>> futureResults = createTopicsResult.values();
        futureResults.values().forEach(f -> {
            try {
                f.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        });
        adminClient.close();
    }

    protected Properties getKafkaProperties() {
        Properties props = new Properties();
        String bootstrapServers = getExternalBootstrapServers();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put("acks", "all");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KAFKA_SERIALIZER);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KAFKA_SERIALIZER);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KAFKA_DESERIALIZER);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KAFKA_DESERIALIZER);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, EnhancedSystemTest.class.getName());
        return props;
    }


    private List<NewTopic> getTopics() {
        return getTopicNames().stream()
                              .map(n -> new NewTopic(n, 1, (short) 1))
                              .collect(Collectors.toList());
    }

    private List<String> getTopicNames() {
        List<String> topicNames = new ArrayList<>();
        topicNames.add(TOPIC_NAME);
        return topicNames;
    }



    @Test
    void kafkaLinkEnhancedWithMessage() {
        writeRandomMessagesToTopic();
        writeExpectedMessageToKafkaTopic();
        writeRandomMessagesToTopic();

        whenWeRetrieveMessagesForTraceId();

        thenTheExpectedMessageIsRetrieved();
    }

    private void whenWeRetrieveMessagesForTraceId() {
        ChainDecorator chainDecorator = new KafkaChainDecorator(new KafkaTopicConsumer());
        decoratedChain = chainDecorator.decorate(createPlainChain());
    }

    private Chain createPlainChain() {
        Chain chain = new Chain(TRACE_ID);
        Link topicLink = new KafkaTopicLink(TOPIC_NAME);
        chain.add(topicLink);
        return chain;
    }

    private void thenTheExpectedMessageIsRetrieved() {
        Chain expectedChain = new Chain(TRACE_ID);
        KafkaTopicLink topicLink = new KafkaTopicLink(TOPIC_NAME);
        topicLink.key = KEY;
        topicLink.payload = MESSAGE;
        expectedChain.add(topicLink);

        assertThat(decoratedChain).isNotNull();
        assertThat(decoratedChain).isEqualTo(expectedChain);

    }

    protected Properties getKafkaConsumerProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
        props.put("acks", "all");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KAFKA_DESERIALIZER);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KAFKA_DESERIALIZER);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, EnhancedSystemTest.class.getName());
        return props;
    }

    private void writeExpectedMessageToKafkaTopic() {
        sendMessageToKafkaTopic(TOPIC_NAME, KEY, MESSAGE);
    }

    private void writeRandomMessagesToTopic() {
        headers = new ArrayList<>();
        headers.add(createRandomHeader());
        String key = "random Key String: " + new Random().nextInt();
        String value = "random Value String: " + new Random().nextInt();
        sendMessageToKafkaTopic(TOPIC_NAME, key, value);
    }

    private Header createRandomHeader() {
        String s = "banana" + new Random().nextInt();
        byte[] randomTrace = s.getBytes();
        return new RecordHeader(TRACING_HEADER, randomTrace);
    }

    private void sendMessageToKafkaTopic(String topic, String key, String value) {
        try {
            getStringStringKafkaProducer().send(createProducerRecord(topic, key, value)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private KafkaProducer<String, String> getStringStringKafkaProducer() {
        return new KafkaProducer<>(kafkaPropertiesForProducer());
    }

    private Properties kafkaPropertiesForProducer() {
        Properties props = new Properties();
        props.put("acks", "all");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getExternalBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KAFKA_SERIALIZER);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KAFKA_SERIALIZER);
        return props;
    }

    private String getExternalBootstrapServers() {
        return KAFKA_CONTAINER.getBootstrapServers();
    }

    private ProducerRecord createProducerRecord(String topicName, String key, String value) {
        return new ProducerRecord(topicName, NO_PARTITION, key, value, headers);
    }

}
