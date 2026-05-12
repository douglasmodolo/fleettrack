package com.fleettrack.order.presentation;

import com.fleettrack.order.application.port.in.CreateOrderCommand;
import com.fleettrack.order.application.port.in.CreateOrderUseCase;
import com.fleettrack.order.domain.model.Order;
import com.fleettrack.order.presentation.dto.CreateOrderRequest;
import com.fleettrack.order.presentation.dto.OrderResponse;
import com.fleettrack.order.presentation.mapper.OrderPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
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
}
