package com.fleettrack.order.application.port.in;

import com.fleettrack.order.domain.model.Order;

public interface AssignOrderDriverUseCase {
    Order execute(AssignOrderDriverCommand command);
}
