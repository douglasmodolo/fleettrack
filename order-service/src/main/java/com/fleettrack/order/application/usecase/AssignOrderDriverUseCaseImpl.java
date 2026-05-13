package com.fleettrack.order.application.usecase;

import com.fleettrack.order.application.port.in.AssignOrderDriverCommand;
import com.fleettrack.order.application.port.in.AssignOrderDriverUseCase;
import com.fleettrack.order.application.port.out.OrderRepositoryPort;
import com.fleettrack.order.domain.exception.OrderNotFoundException;
import com.fleettrack.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignOrderDriverUseCaseImpl implements AssignOrderDriverUseCase {

    private final OrderRepositoryPort repositoryPort;

    @Override
    public Order execute(AssignOrderDriverCommand command) {
        Order order = repositoryPort.findById(command.getId())
                .orElseThrow(() -> new OrderNotFoundException(command.getId()));

        order.assignDriver(command.getDriverId());

        return repositoryPort.save(order);
    }
}
