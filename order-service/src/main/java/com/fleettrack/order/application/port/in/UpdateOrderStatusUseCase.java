package com.fleettrack.order.application.port.in;

import com.fleettrack.order.domain.model.Order;

public interface UpdateOrderStatusUseCase {
    Order execute(UpdateOrderStatusCommand command);
}
