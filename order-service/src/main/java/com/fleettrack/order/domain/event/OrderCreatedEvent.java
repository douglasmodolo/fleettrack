package com.fleettrack.order.domain.event;

import com.fleettrack.order.domain.model.OrderStatus;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

// Evento de domínio — representa o fato de que um pedido foi criado.
// Fica no domain porque é um conceito de negócio, não de infraestrutura.
// Qualquer camada pode reagir a esse evento sem conhecer o mecanismo de publicação.
@Value
public class OrderCreatedEvent {
    UUID orderId;
    String customerName;
    OrderStatus status;
    LocalDateTime createdAt;
    LocalDateTime estimatedDeliveryAt;
}
