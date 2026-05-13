package com.fleettrack.order.domain.exception;

import com.fleettrack.order.domain.model.OrderStatus;

// Exceção de domínio — representa uma transição de status inválida.
// Fica no domain porque é uma regra de negócio — o domínio define
// quais transições são permitidas, não o controller nem o caso de uso.
public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Invalid status transition from " + from + " to " + to);
    }
}