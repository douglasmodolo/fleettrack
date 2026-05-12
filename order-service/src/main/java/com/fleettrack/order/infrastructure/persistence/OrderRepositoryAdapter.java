package com.fleettrack.order.infrastructure.persistence;

import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository repository;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = OrderMapper.fromDomain(order);

        OrderJpaEntity createdEntity = repository.save(entity);

        return OrderMapper.toDomain(createdEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        // map() converte Optional<OrderJpaEntity> para Optional<Order>
        // se vazio, retorna Optional.empty() automaticamente
        return repository.findById(id).map(OrderMapper::toDomain);
    }
}
