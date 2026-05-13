package com.fleettrack.order.application.port.in;

import com.fleettrack.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllOrdersUseCase {
    Page<Order> execute(Pageable pageable);
}
