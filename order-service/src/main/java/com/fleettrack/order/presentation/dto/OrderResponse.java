package com.fleettrack.order.presentation.dto;

import com.fleettrack.order.domain.model.OrderStatus;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

// DTO de saída — representa o pedido como o cliente recebe na resposta HTTP.
// Sem validações — dados já foram validados na entrada.
// @Value — imutável, construtor completo gerado pelo Lombok.
@Value
public class OrderResponse {
    UUID id;
    OrderStatus status;
    LocalDateTime createdAt;
    LocalDateTime estimatedDeliveryAt;
    AddressResponse destinationAddress;
}
