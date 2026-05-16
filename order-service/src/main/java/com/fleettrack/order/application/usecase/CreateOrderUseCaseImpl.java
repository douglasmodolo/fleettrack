package com.fleettrack.order.application.usecase;

import com.fleettrack.order.application.port.in.CreateOrderCommand;
import com.fleettrack.order.application.port.in.CreateOrderUseCase;
import com.fleettrack.order.application.port.out.OrderEventPublisherPort;
import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.event.OrderCreatedEvent;
import com.fleettrack.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// @Service marca essa classe como componente de lógica de negócio.
// Semanticamente mais correto que @Component para casos de uso.
@Service
// Gera construtor com todos os campos final — forma correta de
// injetar dependências. Evita @Autowired em campo.
@RequiredArgsConstructor
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    // final — dependência obrigatória e imutável.
    // O caso de uso depende da interface, nunca da implementação concreta.
    // O Spring injeta o OrderRepositoryAdapter automaticamente.
    private final OrderRepositoryPort repositoryPort;

    private final OrderEventPublisherPort eventPublisherPort;

    @Override
    public Order execute(CreateOrderCommand command) {
        // Delega a criação ao factory method do domínio.
        // O caso de uso não sabe como um Order é criado —
        // essa regra pertence ao domain.
        Order order = Order.create(
                command.getCustomerName(),
                command.getOriginAddress(),
                command.getDestinationAddress()
        );

        // Persiste via port — o caso de uso não sabe que existe JPA,
        // PostgreSQL ou qualquer outro mecanismo de persistência.
        Order savedOrder = repositoryPort.save(order);

        // monta o evento com os dados do pedido salvo
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerName(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt(),
                savedOrder.getEstimatedDeliveryAt()
        );

        // publica — o caso de uso não sabe que é Kafka, só chama a interface
        eventPublisherPort.publishOrderCreated(event);

        return savedOrder;
    }
}