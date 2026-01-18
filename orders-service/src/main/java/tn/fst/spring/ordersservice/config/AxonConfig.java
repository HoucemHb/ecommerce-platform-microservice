// ===========================================
// ORDERS SERVICE - Axon Configuration (FIXED)
// File: orders-service/src/main/java/tn/fst/spring/ordersservice/config/AxonConfig.java
// ===========================================

package tn.fst.spring.ordersservice.config;

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public SnapshotTriggerDefinition orderAggregateSnapshotTriggerDefinition(
            Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }
}