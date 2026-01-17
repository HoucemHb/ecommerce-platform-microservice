package tn.fst.spring.notificationsservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.messaging.StreamableMessageSource;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public Serializer serializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                .objectMapper(objectMapper)
                .build();
    }

    @Bean
    public TokenStore tokenStore(Serializer serializer, EntityManager entityManager) {
        return JpaTokenStore.builder()
                .entityManagerProvider(() -> entityManager)
                .serializer(serializer)
                .build();
    }

    @Autowired
    public void configure(EventProcessingConfigurer configurer, EventStore eventStore) {

        // 1️⃣ Tracking Processor : notification-handler
        configurer.registerTrackingEventProcessor(
                "notification-handler",

                // SOURCE: must return StreamableMessageSource<TrackedEventMessage<?>>
                conf -> (StreamableMessageSource<TrackedEventMessage<?>>) eventStore,

                // CONFIGURATION
                conf -> TrackingEventProcessorConfiguration
                        .forParallelProcessing(3)
                        .andBatchSize(20)
        );

        // 2️⃣ Error handler
        configurer.registerListenerInvocationErrorHandler(
                "notification-handler",
                conf -> (exception, event, handler) -> {
                    System.err.println(
                            "[NOTIFICATION ERROR] " + exception.getMessage()
                    );
                }
        );
    }
}
