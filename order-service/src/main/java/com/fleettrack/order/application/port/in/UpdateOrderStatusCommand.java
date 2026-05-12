package com.fleettrack.order.application.port.in;

import com.fleettrack.order.domain.model.OrderStatus;
import lombok.Value;

import java.util.UUID;

@Value
public class UpdateOrderStatusCommand {
    UUID id;
    OrderStatus status;
}
