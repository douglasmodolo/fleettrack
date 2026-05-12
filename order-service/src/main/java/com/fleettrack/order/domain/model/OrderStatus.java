package com.fleettrack.order.domain.model;

import java.util.Optional;

// Enum representa os estados possíveis de um pedido.
// Fica no domain porque é uma regra de negócio — o sistema
// define quais status existem, não o banco nem o HTTP.
public enum OrderStatus {
    PENDING,    // pedido criado, aguardando coleta
    PICKED_UP,  // coletado pelo entregador
    IN_TRANSIT, // em rota de entrega
    DELIVERED,  // entregue com sucesso
    CANCELLED;  // cancelado — ponto e vírgula obrigatório quando há métodos no enum

    // Converte uma String para OrderStatus de forma segura.
    // Usado na deserialização de eventos Kafka e em filtros de busca.
    // Retorna Optional vazio se o valor não corresponder a nenhum status
    // conhecido — evita IllegalArgumentException em tempo de execução.
    public static Optional<OrderStatus> fromString(String value) {
        for (OrderStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}