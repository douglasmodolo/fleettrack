package com.fleettrack.order.application.usecase;

import com.fleettrack.order.application.port.in.UpdateOrderStatusCommand;
import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.exception.InvalidOrderStatusTransitionException;
import com.fleettrack.order.domain.exception.OrderNotFoundException;
import com.fleettrack.order.domain.model.Address;
import com.fleettrack.order.domain.model.Order;
import com.fleettrack.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateOrderStatusUseCaseImplTest {

    @Mock
    private OrderRepositoryPort repositoryPort;

    @InjectMocks
    private UpdateOrderStatusUseCaseImpl useCase;

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

    // Método auxiliar — cria um Order já existente no banco via reconstitute.
    // Usado nos testes para simular o que o repositório retornaria.
    private Order buildExistingOrder(OrderStatus status) {
        return Order.reconstitute(
                UUID.randomUUID(),
                0L,                          // version começa em 0
                "Douglas Silva",
                buildAddress(),
                buildAddress(),
                null,                        // sem driver ainda
                status,                      // status configurável por teste
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                null,                        // pickedUpAt nulo
                null                         // deliveredAt nulo
        );
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        // given — pedido existente no banco com status PENDING
        Order existingOrder = buildExistingOrder(OrderStatus.PENDING);
        UUID orderId = existingOrder.getId();

        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(
                orderId,
                OrderStatus.PICKED_UP
        );

        // configura o mock: findById retorna o pedido existente
        when(repositoryPort.findById(orderId))
                .thenReturn(Optional.of(existingOrder));

        // configura o mock: save devolve o mesmo Order que recebeu
        when(repositoryPort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = useCase.execute(command);

        // then — status foi atualizado corretamente
        assertEquals(OrderStatus.PICKED_UP, result.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // given — repositório não encontra nenhum pedido
        UUID randomId = UUID.randomUUID();

        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(
                randomId,
                OrderStatus.PICKED_UP
        );

        // configura o mock: findById retorna vazio — pedido não existe
        when(repositoryPort.findById(randomId))
                .thenReturn(Optional.empty());

        // when / then — verifica que OrderNotFoundException foi lançada
        assertThrows(OrderNotFoundException.class, () ->
                useCase.execute(command)
        );
    }

    @Test
    void shouldThrowExceptionWhenStatusTransitionIsInvalid() {
        // given — pedido já entregue, não pode mudar mais
        Order existingOrder = buildExistingOrder(OrderStatus.DELIVERED);
        UUID orderId = existingOrder.getId();

        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(
                orderId,
                OrderStatus.PENDING  // tentativa de voltar para PENDING — inválido
        );

        // configura o mock: findById retorna o pedido entregue
        when(repositoryPort.findById(orderId))
                .thenReturn(Optional.of(existingOrder));

        // when / then — verifica que InvalidOrderStatusTransitionException foi lançada
        // o save não deve ser chamado porque a exceção acontece antes
        assertThrows(InvalidOrderStatusTransitionException.class, () ->
                useCase.execute(command)
        );
    }
}
