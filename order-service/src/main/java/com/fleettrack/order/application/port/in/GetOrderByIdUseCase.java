package com.fleettrack.order.application.port.in;

import com.fleettrack.order.domain.model.Order;

import java.util.UUID;

public interface GetOrderByIdUseCase {
    Order execute(UUID id);
}
