package com.fleettrack.order.presentation.dto;

import lombok.Value;

// DTO de saída — representa o pedido como o cliente recebe na resposta HTTP.
// Sem validações — dados já foram validados na entrada.
// @Value — imutável, construtor completo gerado pelo Lombok.
@Value
public class AddressResponse {
    String country;
    String state;
    String city;
    String zipCode;
    String street;
    String number;
    String complement;
}
