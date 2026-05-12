package com.fleettrack.order.presentation.dto;

import com.fleettrack.order.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    OrderStatus status;
}
