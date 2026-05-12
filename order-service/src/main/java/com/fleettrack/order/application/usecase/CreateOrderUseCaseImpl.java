package com.fleettrack.order.application.usecase;

import com.fleettrack.order.application.port.in.CreateOrderCommand;
import com.fleettrack.order.application.port.in.CreateOrderUseCase;
import com.fleettrack.order.application.port.out.OrderRepositoryPort;
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
        return repositoryPort.save(order);
    }
}