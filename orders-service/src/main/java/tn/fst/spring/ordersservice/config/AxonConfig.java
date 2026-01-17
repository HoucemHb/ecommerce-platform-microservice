package tn.fst.spring.ordersservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.jpa.JpaSagaStore;
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
    public SagaStore sagaStore(Serializer serializer, EntityManager entityManager) {
        return JpaSagaStore.builder()
                .entityManagerProvider(() -> entityManager)
                .serializer(serializer)
                .build();
    }

    @Bean
    public SnapshotTriggerDefinition snapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 50);
    }

    // ===============================
    // CONFIGURE TRACKING PROCESSORS
    // ===============================
    @Autowired
    public void configure(EventProcessingConfigurer configurer, EventStore eventStore) {

        // 1️⃣ Tracking Processor for Order Projection
        configurer.registerTrackingEventProcessor(
                "order-projection",
                conf -> eventStore,      // <-- SOURCE FIXED
                conf -> TrackingEventProcessorConfiguration
                        .forParallelProcessing(4)
                        .andBatchSize(100)
        );

        // 2️⃣ Register the Saga Processor
        configurer.registerSaga(
                tn.fst.spring.ordersservice.saga.OrderManagementSaga.class,
                sagaConfigurer -> sagaConfigurer.configureSagaStore(c ->
                        sagaStore(c.serializer(), c.getComponent(EntityManager.class))
                )
        );

        // 3️⃣ Default Error Handler
        configurer.registerDefaultListenerInvocationErrorHandler(
                conf -> (exception, event, eventHandler) -> {
                    System.err.println("Handler error: " + exception.getMessage());
                    throw exception; // retry
                }
        );
    }

    // ===============================
    // CUSTOM COMMAND BUS
    // ===============================
    @Bean
    public CommandBus commandBus(TransactionManager transactionManager) {

        SimpleCommandBus commandBus = SimpleCommandBus.builder()
                .transactionManager(transactionManager)
                .build();

        // Bean Validation: @NotNull, @Size, etc.
        commandBus.registerDispatchInterceptor(new BeanValidationInterceptor<>());

        return commandBus;
    }
}
