package com.fleettrack.order.application.port.out;

import com.fleettrack.order.domain.event.OrderCreatedEvent;

// Port de saída — define o contrato de publicação de eventos.
// O caso de uso depende dessa interface, nunca do Kafka diretamente.
// A infrastructure implementa usando o KafkaTemplate.
public interface OrderEventPublisherPort {
    void publishOrderCreated(OrderCreatedEvent event);
}
