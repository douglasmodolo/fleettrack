package com.fleettrack.order.application.usecase;

import com.fleettrack.order.application.port.in.GetOrderByIdUseCase;
import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.exception.OrderNotFoundException;
import com.fleettrack.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrderByIdUseCaseImpl implements GetOrderByIdUseCase {

    private final OrderRepositoryPort repositoryPort;

    @Override
    public Order execute(UUID id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
