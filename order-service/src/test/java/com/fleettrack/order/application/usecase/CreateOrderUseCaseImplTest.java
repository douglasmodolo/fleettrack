package com.fleettrack.order.application.usecase;

import com.fleettrack.order.application.port.in.CreateOrderCommand;
import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.model.Address;
import com.fleettrack.order.domain.model.Order;
import com.fleettrack.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateOrderUseCaseImplTest {
    @Mock
    private OrderRepositoryPort repositoryPort;

    @InjectMocks
    private CreateOrderUseCaseImpl useCase;

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
    void shouldCreateOrderSuccessfully() {
        // given
        CreateOrderCommand command = new CreateOrderCommand("Douglas Silva", buildAddress(), buildAddress());

        // configura o mock: quando save() for chamado com qualquer Order,
        // retorna o mesmo Order que foi passado — simula o comportamento real
        when(repositoryPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = useCase.execute(command);

        // then
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals("Douglas Silva", result.getCustomerName());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotSaved() {
        // given
        CreateOrderCommand command = new CreateOrderCommand("Douglas Silva", buildAddress(), buildAddress());

        when(repositoryPort.save(any(Order.class)))
                .thenThrow(new RuntimeException("Database error"));

        // when / then — verifica que a exceção foi propagada
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                useCase.execute(command)
        );

        assertEquals("Database error", exception.getMessage());
    }
}
