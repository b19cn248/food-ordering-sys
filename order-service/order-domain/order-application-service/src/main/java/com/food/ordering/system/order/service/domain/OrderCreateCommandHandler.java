package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;

    private final OrderDataMapper orderDataMapper;

    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {

        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);

        log.info("Order created with id:{}", orderCreatedEvent.getOrder().getId().getValue());

        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);

        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "Order created successfully.");

    }

}
