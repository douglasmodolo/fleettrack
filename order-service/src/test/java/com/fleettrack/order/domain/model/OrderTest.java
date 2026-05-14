package com.fleettrack.order.domain.model;

import com.fleettrack.order.domain.exception.InvalidOrderStatusTransitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    // Método auxiliar — evita repetir a criação do Address em todo teste
    private Address buildAddress() {
        return Address.builder()
                .country("Brasil")
                .state("SP")
                .city("São Paulo")
                .zipCode("01310-100")
                .street("Avenida Paulista")
                .number("1000")
                .build();
    }

    @Test
    void shouldCreateOrderWithPendingStatus() {
        // given
        Order order = Order.create("Douglas Silva", buildAddress(), buildAddress());

        // when / then
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void shouldGenerateIdOnCreate() {
        // given
        Order order = Order.create("Douglas Silva", buildAddress(), buildAddress());

        // when / then
        assertNotNull(order.getId());
    }

    @Test
    void shouldSetCreatedAtOnCreate() {
        // given
        Order order = Order.create("Douglas Silva", buildAddress(), buildAddress());

        // when / then
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void shouldCalculateEstimatedDeliveryAt() {
        // given
        Order order = Order.create("Douglas Silva", buildAddress(), buildAddress());

        // when / then
        assertEquals(order.getCreatedAt().plusDays(3), order.getEstimatedDeliveryAt());
    }

    @Test
    void shouldAllowValidStatusTransition() {
        // given
        Order order = Order.create("Douglas Silva", buildAddress(), buildAddress());

        // when
        order.updateStatus(OrderStatus.CANCELLED);

        // then
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldRejectInvalidStatusTransition() {
        // given
        Order order = Order.create("Douglas Silva", buildAddress(), buildAddress());
        order.updateStatus(OrderStatus.DELIVERED); // coloca em estado final

        // when / then — tenta qualquer transição a partir de DELIVERED
        assertThrows(InvalidOrderStatusTransitionException.class, () ->
                order.updateStatus(OrderStatus.CANCELLED)
        );
    }
}
