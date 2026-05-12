package com.fleettrack.order.application.port.in;

import com.fleettrack.order.domain.model.Address;
import lombok.Value;

// Comando — transporta os dados brutos da requisição até o caso de uso.
// Não tem lógica, não cria objetos, não valida regras de negócio.
// @Value gera: construtor completo, getters, campos final e imutabilidade.
@Value
public class CreateOrderCommand {
    String customerName;
    Address originAddress;
    Address destinationAddress;
}
