package com.fleettrack.order.infrastructure.persistence;

import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository repository;

    @Override
    public Order save(Order order) {
        boolean exists = repository.existsById(order.getId());
        OrderJpaEntity entity = exists
                ? OrderMapper.fromDomainForUpdate(order)
                : OrderMapper.fromDomain(order);
        return OrderMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Order> findById(UUID id) {
        // map() converte Optional<OrderJpaEntity> para Optional<Order>
        // se vazio, retorna Optional.empty() automaticamente
        return repository.findById(id).map(OrderMapper::toDomain);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        // map() aplica a conversão em cada elemento da página
        // mantendo as informações de paginação (total, página atual, etc.)
        return repository.findAll(pageable).map(OrderMapper::toDomain);
    }
}
