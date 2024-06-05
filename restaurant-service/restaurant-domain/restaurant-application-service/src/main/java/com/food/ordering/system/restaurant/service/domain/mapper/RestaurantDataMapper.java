package com.food.ordering.system.restaurant.service.domain.mapper;


import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestaurantDataMapper {

  public Restaurant restaurantApprovalRequestToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {

    OrderDetail orderDetail = OrderDetail.builder()
          .products(restaurantApprovalRequest.getProducts().stream()
                .map(product -> Product.builder()
                      .productId(product.getId())
                      .name(product.getName())
                      .price(product.getPrice())
                      .quantity(product.getQuantity())
                      .build()
                )
                .toList()
          )
          .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
          .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
          .build();

    orderDetail.setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));

    Restaurant restaurant = Restaurant.builder()
          .orderDetail(orderDetail)
          .build();

    restaurant.setId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())));

    return restaurant;
  }
}
