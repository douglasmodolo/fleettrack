package com.fleettrack.order.domain.exception;

import java.util.UUID;

// Exceção de domínio — representa uma regra de negócio violada:
// tentou operar em um pedido que não existe.
// Estende RuntimeException para não forçar try/catch no chamador.
// Será capturada na camada de presentation e transformada em HTTP 404.
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(UUID id) {
        super("Order not found with id: " + id);
    }
}