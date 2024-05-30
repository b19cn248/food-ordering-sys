package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregationRoot;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class Order extends AggregationRoot<OrderId> {

  private final CustomerId customerId;
  private final RestaurantId restaurantId;
  private final StreetAddress deliveryAddress;
  private final Money price;
  private final List<OrderItem> items;


  private TrackingId trackingId;
  private OrderStatus orderStatus;
  private List<String> failureMessages;

  public void initializeOrder() {
    setId(new OrderId(UUID.randomUUID()));
    trackingId = new TrackingId(UUID.randomUUID());
    orderStatus = OrderStatus.PENDING;
    initializedOrderItems();
  }


  public void validateOrder() {
    validateInitialOrder();
    validateTotalPrice();
    validateItemsPrice();
  }

  public void pay() {
    if (orderStatus != OrderStatus.PENDING) {
      throw new OrderDomainException("Order is not pending");
    }

    orderStatus = OrderStatus.PAID;
  }

  public void approve() {
    if (orderStatus != OrderStatus.PAID) {
      throw new OrderDomainException("Order is not paid");
    }

    orderStatus = OrderStatus.APPROVED;
  }

  public void initCancel(List<String> failureMessages) {
    if (orderStatus != OrderStatus.PAID) {
      throw new OrderDomainException("Order is not paid");
    }

    orderStatus = OrderStatus.CANCELLING;
    updateFailureMessages(failureMessages);
  }

  private void updateFailureMessages(List<String> failureMessages) {
    if (this.failureMessages != null && failureMessages != null) {
      this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
    } else {
      this.failureMessages = failureMessages;
    }
  }

  public void cancel(List<String> failureMessages) {
    if (!(orderStatus == OrderStatus.CANCELLING || orderStatus == OrderStatus.PENDING)) {
      throw new OrderDomainException("Order is not in correct state for cancel operation!");
    }
    orderStatus = OrderStatus.CANCELLED;
    updateFailureMessages(failureMessages);
  }

  private void validateInitialOrder() {
    if (orderStatus != null && getId() != null) {
      throw new OrderDomainException("Order is already initialized");
    }
  }

  private void validateTotalPrice() {
    if (price == null || !price.isGreaterThanZero()) {
      throw new OrderDomainException("Total price must be greater than zero");
    }
  }

  private void validateItemsPrice() {
    Money orderItemsTotal = items.stream().map(orderItem -> {
      validateItemPrice(orderItem);
      return orderItem.getSubTotal();
    }).reduce(Money.ZERO, Money::add);

    if (!price.equals(orderItemsTotal)) {
      throw new OrderDomainException("Total price: " + price.amount()
            + " is not equal to the sum of items price: " + orderItemsTotal.amount());
    }
  }

  private void validateItemPrice(OrderItem orderItem) {

    if (!orderItem.isPriceValid()) {
      throw new OrderDomainException("Order item price " + orderItem.getPrice().amount()
            + " is not valid for product: " + orderItem.getProduct().getId().getValue());
    }
  }


  private void initializedOrderItems() {
    long itemId = 1;
    for (OrderItem item : items) {
      item.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Order order = (Order) o;
    return Objects.equals(customerId, order.customerId) && Objects.equals(restaurantId, order.restaurantId) && Objects.equals(deliveryAddress, order.deliveryAddress) && Objects.equals(price, order.price) && Objects.equals(items, order.items) && Objects.equals(trackingId, order.trackingId) && orderStatus == order.orderStatus && Objects.equals(failureMessages, order.failureMessages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), customerId, restaurantId, deliveryAddress, price, items, trackingId, orderStatus, failureMessages);
  }

  @Override
  public String toString() {
    return "Order{" +
          "items=" + items +
          '}';
  }
}
