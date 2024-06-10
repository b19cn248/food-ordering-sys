package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregationRoot;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class Restaurant extends AggregationRoot<RestaurantId> {

  private OrderApproval orderApproval;
  private boolean active;
  private final OrderDetail orderDetail;

  public void validateOrder(List<String> failureMessages) {
    if (orderDetail.getOrderStatus() != OrderStatus.PAID) {
      failureMessages.add("Payment is not completed for order : " + orderDetail.getId());
    }

    Money totalAmount = orderDetail.getProducts().stream()
          .map(product -> {
            if (!product.isAvailable()) {
              failureMessages.add("Product with id : " + product.getId().getValue() + " is not available");
            }
            return product.getPrice().multiply(product.getQuantity());
          }).reduce(Money.ZERO, Money::add);

    if (!totalAmount.equals(orderDetail.getTotalAmount())) {
      failureMessages.add("Total amount is not correct for order : " + orderDetail.getId());
    }
  }

  public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
    OrderApproval orderApproval = OrderApproval.builder()
          .restaurantId(getId())
          .orderId(orderDetail.getId())
          .approvalStatus(orderApprovalStatus)
          .build();

    orderApproval.setId(new OrderApprovalId(UUID.randomUUID()));

    this.orderApproval = orderApproval;
  }

}
