package com.fleettrack.order.infrastructure.messaging;

import com.fleettrack.order.application.port.out.OrderEventPublisherPort;
import com.fleettrack.order.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Implementação da port de publicação de eventos via Kafka.
// Fica na infrastructure porque Kafka é um detalhe de infraestrutura.
// O caso de uso não sabe que existe Kafka — só conhece a interface.
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisherPort {

    // tópico onde os eventos de criação de pedido serão publicados
    private static final String ORDER_CREATED_TOPIC = "order.created";

    // KafkaTemplate é o componente do Spring para enviar mensagens
    // a chave é String (o orderId), o valor é o evento serializado em JSON
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent for orderId: {}", event.getOrderId());

        // send(topic, key, value)
        // a chave é o orderId — garante que eventos do mesmo pedido
        // vão para a mesma partição, mantendo a ordem dos eventos
        kafkaTemplate.send(ORDER_CREATED_TOPIC, event.getOrderId().toString(), event);

        log.info("OrderCreatedEvent published successfully for orderId: {}", event.getOrderId());
    }
}
