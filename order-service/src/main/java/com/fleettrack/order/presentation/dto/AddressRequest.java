package com.fleettrack.order.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AddressRequest {

    @NotBlank(message = "Country is required")
    String country;

    @NotBlank(message = "State is required")
    String state;

    @NotBlank(message = "City is required")
    String city;

    @NotBlank(message = "Zip code is required")
    String zipCode;

    @NotBlank(message = "Street is required")
    String street;

    @NotBlank(message = "Number is required")
    String number;

    // Opcional — sem validação. O cliente pode ou não enviar.
    String complement;
}