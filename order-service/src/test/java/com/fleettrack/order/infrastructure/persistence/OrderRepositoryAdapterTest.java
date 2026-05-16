package com.fleettrack.order.infrastructure.persistence;

import com.fleettrack.order.domain.model.Address;
import com.fleettrack.order.domain.model.Order;
import com.fleettrack.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class OrderRepositoryAdapterTest {

    @Autowired
    private OrderRepositoryAdapter repository;

    @Test
    void shouldSaveAndFindOrder() {
        // given — cria um Order usando o factory method do domain
        Address address = Address.builder()
                .country("Brasil")
                .state("SP")
                .city("São Paulo")
                .zipCode("01310-100")
                .street("Avenida Paulista")
                .number("1000")
                .build();

        Order order = Order.create("Douglas Silva", address, address);

        // when
        repository.save(order);

        // then — busca pelo id e verifica que persistiu corretamente
        Optional<Order> found = repository.findById(order.getId());

        assertTrue(found.isPresent());
        assertEquals("Douglas Silva", found.get().getCustomerName());
        assertEquals(OrderStatus.PENDING, found.get().getStatus());
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // given — id que não existe no banco
        UUID randomId = UUID.randomUUID();

        // when
        Optional<Order> found = repository.findById(randomId);

        // then — deve retornar vazio
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldUpdateOrderStatus() {
        // given — cria e salva um pedido
        Address address = Address.builder()
                .country("Brasil")
                .state("SP")
                .city("São Paulo")
                .zipCode("01310-100")
                .street("Avenida Paulista")
                .number("1000")
                .build();

        Order order = Order.create("Douglas Silva", address, address);

        // usa o order retornado pelo save — contém o version atualizado
        Order savedOrder = repository.save(order);

        // when — atualiza o status no order que tem o version correto
        savedOrder.updateStatus(OrderStatus.PICKED_UP);
        repository.save(savedOrder);

        // then
        Optional<Order> found = repository.findById(savedOrder.getId());
        assertTrue(found.isPresent());
        assertEquals(OrderStatus.PICKED_UP, found.get().getStatus());
    }
}
