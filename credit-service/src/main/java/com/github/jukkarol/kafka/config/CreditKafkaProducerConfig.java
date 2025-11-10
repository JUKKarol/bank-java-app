package com.github.jukkarol.kafka.config;

import com.github.jukkarol.dto.creditDto.event.request.CreditRequestEvent;
import com.github.jukkarol.dto.creditDto.event.response.CreditResponseEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CreditKafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, CreditRequestEvent> requestProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConsumerFactory<String, CreditResponseEvent> responseConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "credit-service-reply-group");

        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.github.jukkarol.dto.creditDto.event");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CreditResponseEvent.class);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public KafkaMessageListenerContainer<String, CreditResponseEvent> replyContainer(
            ConsumerFactory<String, CreditResponseEvent> consumerFactory) {

        ContainerProperties containerProperties = new ContainerProperties("credit-responses");
        containerProperties.setGroupId("credit-service-reply-group");
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, CreditRequestEvent, CreditResponseEvent> replyingKafkaTemplate(
            ProducerFactory<String, CreditRequestEvent> producerFactory,
            KafkaMessageListenerContainer<String, CreditResponseEvent> container) {

        ReplyingKafkaTemplate<String, CreditRequestEvent, CreditResponseEvent> template =
                new ReplyingKafkaTemplate<>(producerFactory, container);

        template.setDefaultTopic("credit-requests");
        template.setDefaultReplyTimeout(Duration.ofSeconds(10));
        template.setSharedReplyTopic(true);

        return template;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, Object> eventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
