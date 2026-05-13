package com.fleettrack.order.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Getter
@Builder
@Jacksonized
public class AssignOrderDriverRequest {

    @NotNull(message = "Driver id is required")
    UUID driverId;
}
