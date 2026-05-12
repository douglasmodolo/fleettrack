package com.fleettrack.order.application.usecase;

import com.fleettrack.order.application.port.in.UpdateOrderStatusCommand;
import com.fleettrack.order.application.port.in.UpdateOrderStatusUseCase;
import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.exception.OrderNotFoundException;
import com.fleettrack.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusUseCaseImpl implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort repositoryPort;

    @Override
    public Order execute(UpdateOrderStatusCommand command) {
        Order order = repositoryPort.findById(command.getId())
                .orElseThrow(() -> new OrderNotFoundException(command.getId()));

        order.updateStatus(command.getStatus());

        return repositoryPort.save(order);
    }
}
