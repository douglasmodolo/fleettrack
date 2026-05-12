package com.fleettrack.order.presentation.mapper;

import com.fleettrack.order.application.port.in.CreateOrderCommand;
import com.fleettrack.order.application.port.in.UpdateOrderStatusCommand;
import com.fleettrack.order.domain.model.Address;
import com.fleettrack.order.domain.model.Order;
import com.fleettrack.order.presentation.dto.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Mapper da camada presentation — converte entre DTOs HTTP e objetos do domain/application.
// Existe para isolar a apresentação do domínio — o controller não monta objetos manualmente.
@Component
public class OrderPresentationMapper {

    // Converte o request HTTP em comando para o caso de uso.
    // O caso de uso não conhece DTOs — recebe um comando limpo.
    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(
                request.getCustomerName(),
                toAddressDomain(request.getOriginAddress()),
                toAddressDomain(request.getDestinationAddress())
        );
    }

    public UpdateOrderStatusCommand toCommand(UUID id, UpdateOrderStatusRequest request) {
        return new UpdateOrderStatusCommand(id, request.getStatus());
    }

    // Converte o Order do domain em resposta HTTP.
    // O cliente não recebe o objeto de domínio — recebe um DTO controlado.
    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getEstimatedDeliveryAt(),
                toAddressResponse(order.getDestinationAddress())
        );
    }

    // Método auxiliar — reutilizado para origin e destination.
    // Evita repetir a conversão de AddressRequest para Address duas vezes.
    private Address toAddressDomain(AddressRequest request) {
        return Address.builder()
                .country(request.getCountry())
                .state(request.getState())
                .city(request.getCity())
                .zipCode(request.getZipCode())
                .street(request.getStreet())
                .number(request.getNumber())
                .complement(request.getComplement())
                .build();
    }

    // Método auxiliar — converte Address do domain para AddressResponse.
    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getCountry(),
                address.getState(),
                address.getCity(),
                address.getZipCode(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement().orElse(null)
        );
    }
}