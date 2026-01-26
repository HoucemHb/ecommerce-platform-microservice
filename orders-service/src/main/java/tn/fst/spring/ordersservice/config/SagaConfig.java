package tn.fst.spring.ordersservice.config;

import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.jpa.JpaSagaStore;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.fst.spring.ordersservice.saga.OrderManagementSaga;

@Configuration
public class SagaConfig {

    @Bean
    public SagaStore sagaStore(EntityManagerProvider emp, Serializer serializer) {
        return JpaSagaStore.builder()
                .entityManagerProvider(emp)
                .serializer(serializer)
                .build();
    }

}
