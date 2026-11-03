package dev.sorokin.eventmanager.conf;

import dev.sorokin.eventcommon.kafka.EventPayload;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Value("${topic.name.event}")
    private String topicNameEvent;

    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name(topicNameEvent)
                .partitions(2)
                .build();
    }

    @Bean
    public KafkaTemplate<String, EventPayload> kafkaTemplate(
            KafkaProperties kafkaProperties
    ) {
        ProducerFactory<String, EventPayload> producerFactory =
                new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());

        return new KafkaTemplate<>(producerFactory);
    }
}
