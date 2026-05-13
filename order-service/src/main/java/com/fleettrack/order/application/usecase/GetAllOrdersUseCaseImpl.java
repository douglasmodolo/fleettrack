package com.fleettrack.order.application.usecase;

import com.fleettrack.order.application.port.in.GetAllOrdersUseCase;
import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllOrdersUseCaseImpl implements GetAllOrdersUseCase {

    private final OrderRepositoryPort repositoryPort;

    @Override
    public Page<Order> execute(Pageable pageable) {
        return repositoryPort.findAll(pageable);
    }
}
