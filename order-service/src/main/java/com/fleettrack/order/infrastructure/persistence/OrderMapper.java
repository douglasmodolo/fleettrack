package com.fleettrack.order.infrastructure.persistence;

import com.fleettrack.order.domain.model.Order;

public class OrderMapper {

    public static OrderJpaEntity fromDomain(Order order) {
        return OrderJpaEntity.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .originAddress(AddressEmbeddable.fromDomain(order.getOriginAddress()))
                .destinationAddress(AddressEmbeddable.fromDomain(order.getDestinationAddress()))
                .driverId(order.getDriverId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .estimatedDeliveryAt(order.getEstimatedDeliveryAt())
                .pickedUpAt(order.getPickedUpAt())
                .deliveredAt(order.getDeliveredAt())
                .isNew(true)
                .build();
    }

    public static OrderJpaEntity fromDomainForUpdate(Order order) {
        return OrderJpaEntity.builder()
                .id(order.getId())
                .version(order.getVersion())
                .customerName(order.getCustomerName())
                .originAddress(AddressEmbeddable.fromDomain(order.getOriginAddress()))
                .destinationAddress(AddressEmbeddable.fromDomain(order.getDestinationAddress()))
                .driverId(order.getDriverId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .estimatedDeliveryAt(order.getEstimatedDeliveryAt())
                .pickedUpAt(order.getPickedUpAt())
                .deliveredAt(order.getDeliveredAt())
                .isNew(false)
                .build();
    }

    public static Order toDomain(OrderJpaEntity entity) {
        return Order.reconstitute(
                entity.getId(),
                entity.getVersion(),
                entity.getCustomerName(),
                entity.getOriginAddress().toDomain(),
                entity.getDestinationAddress().toDomain(),
                entity.getDriverId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getEstimatedDeliveryAt(),
                entity.getPickedUpAt(),
                entity.getDeliveredAt()
        );
    }
}
