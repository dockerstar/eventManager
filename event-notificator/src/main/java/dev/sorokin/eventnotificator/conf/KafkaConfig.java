package dev.sorokin.eventnotificator.conf;

import dev.sorokin.eventcommon.kafka.EventPayload;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, EventPayload> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "event-changed");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        var factory = new DefaultKafkaConsumerFactory<String, EventPayload>(props);

        factory.setValueDeserializer(new JsonDeserializer<>(EventPayload.class, false));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventPayload> containerFactory(
            ConsumerFactory<String, EventPayload> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, EventPayload>();
        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}
