package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.event.OrderCancelEvent;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@Slf4j
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {

  private final OrderApprovalSaga orderApprovalSaga;

  public RestaurantApprovalResponseMessageListenerImpl(OrderApprovalSaga orderApprovalSaga) {
    this.orderApprovalSaga = orderApprovalSaga;
  }

  @Override
  public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
    orderApprovalSaga.process(restaurantApprovalResponse);

    log.info("Order with id: {} approved", restaurantApprovalResponse.getOrderId());
  }

  @Override
  public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
    OrderCancelEvent orderCancelEvent = orderApprovalSaga.rollback(restaurantApprovalResponse);
    log.info("Order with id: {} rejected", restaurantApprovalResponse.getOrderId());

    orderCancelEvent.fire();
  }
}
