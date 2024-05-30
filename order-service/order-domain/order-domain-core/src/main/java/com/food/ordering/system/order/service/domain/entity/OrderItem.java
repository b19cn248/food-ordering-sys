package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItem extends BaseEntity<OrderItemId> {

  private OrderId orderId;
  private final Product product;
  private final int quantity;
  private final Money price;
  private final Money subTotal;

  void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
    this.orderId = orderId;
    this.setId(orderItemId);
  }

  boolean isPriceValid() {
    return price.isGreaterThanZero()
          && price.equals(product.getPrice())
          && price.multiply(quantity).equals(subTotal);
  }

  @Override
  public String toString() {
    return "OrderItem{" +
          "orderId=" + orderId +
          ", product=" + product +
          ", quantity=" + quantity +
          ", price=" + price +
          ", subTotal=" + subTotal +
          '}';
  }
}
