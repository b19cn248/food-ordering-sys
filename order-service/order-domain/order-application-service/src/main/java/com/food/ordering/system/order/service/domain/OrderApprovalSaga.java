package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelEvent> {
    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;


    @Override
    @Transactional
    public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {

        log.info("Process approval for order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);

        log.info("Order with id: {} approved", order.getId().getValue());

        return EmptyEvent.INSTANCE;

    }

    @Override
    @Transactional
    public OrderCancelEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Rollback approval for order with id: {}", restaurantApprovalResponse.getOrderId());

        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());

        OrderCancelEvent domainEvent = orderDomainService.cancelOrderPayment(order,
                restaurantApprovalResponse.getFailureMessages(),
                orderCancelledPaymentRequestMessagePublisher);

        orderSagaHelper.saveOrder(order);

        log.info("Order with id: {} payment cancelled", order.getId().getValue());

        return domainEvent;

    }
}
