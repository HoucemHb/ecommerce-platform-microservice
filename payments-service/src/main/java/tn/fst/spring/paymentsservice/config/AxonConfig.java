package tn.fst.spring.paymentsservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
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

    @Bean
    public SnapshotTriggerDefinition snapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 100);
    }

    @Autowired
    public void configure(EventProcessingConfigurer configurer, EventStore eventStore) {

        // 1️⃣ PAYMENT PROJECTION PROCESSOR
        configurer.registerTrackingEventProcessor(
                "payment-projection",
                conf -> eventStore, // SOURCE DES EVENTS
                conf -> TrackingEventProcessorConfiguration
                        .forSingleThreadedProcessing()
        );
    }
}
