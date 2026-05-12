package com.fleettrack.order.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateOrderRequest {

    @NotBlank(message = "Customer name is required")
    String customerName;

    @NotNull(message = "Origin address is required")
    @Valid
    AddressRequest originAddress;

    @NotNull(message = "Destination address is required")
    @Valid
    AddressRequest destinationAddress;
}
