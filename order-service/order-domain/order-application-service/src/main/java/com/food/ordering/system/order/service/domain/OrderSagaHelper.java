package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    public Order findOrder(String orderId) {
        Optional<Order> order = orderRepository.findById(new OrderId(UUID.fromString(orderId)));

        if (order.isEmpty()) {
            log.error("Order with id: {} not found", orderId);
            throw new OrderDomainException("Order with id " + orderId + " not found");
        }

        return order.get();
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
