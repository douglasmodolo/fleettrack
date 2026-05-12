package com.fleettrack.order.presentation;

import com.fleettrack.order.application.port.in.*;
import com.fleettrack.order.domain.model.Order;
import com.fleettrack.order.presentation.dto.CreateOrderRequest;
import com.fleettrack.order.presentation.dto.OrderResponse;
import com.fleettrack.order.presentation.dto.UpdateOrderStatusRequest;
import com.fleettrack.order.presentation.mapper.OrderPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final OrderPresentationMapper orderPresentationMapper;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = orderPresentationMapper.toCommand(request);
        Order order = createOrderUseCase.execute(command);
        OrderResponse response = orderPresentationMapper.toResponse(order);

        return ResponseEntity
                .created(URI.create("/orders/" + order.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("id") UUID id) {
        Order order =  getOrderByIdUseCase.execute(id);
        OrderResponse response = orderPresentationMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@Valid @RequestBody UpdateOrderStatusRequest request, @PathVariable("id") UUID id) {
        UpdateOrderStatusCommand command = orderPresentationMapper.toCommand(id, request);
        Order order = updateOrderStatusUseCase.execute(command);
        OrderResponse response = orderPresentationMapper.toResponse(order);
        return ResponseEntity.ok(response);
    }
}
