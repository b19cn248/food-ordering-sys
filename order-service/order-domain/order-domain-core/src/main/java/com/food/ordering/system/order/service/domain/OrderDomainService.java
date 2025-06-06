package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

  OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant,
                                             DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher);

  OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher);

  void approveOrder(Order order);

  OrderCancelEvent cancelOrderPayment(Order order, List<String> failureMessages, DomainEventPublisher<OrderCancelEvent> orderCancelEventDomainEventPublisher);

  void cancelOrder(Order order, List<String> failureMessages);
}
