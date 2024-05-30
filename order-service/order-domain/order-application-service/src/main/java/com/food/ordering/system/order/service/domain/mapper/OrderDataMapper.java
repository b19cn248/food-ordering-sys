package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {

  public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
    Restaurant restaurant = Restaurant.builder()
          .products(createOrderCommand.items().stream().map(
                item -> new Product(new ProductId(item.productId()))).toList())
          .build();

    restaurant.setId(new RestaurantId(createOrderCommand.restaurantId()));

    return restaurant;
  }


  public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
    return Order.builder()
          .customerId(new CustomerId(createOrderCommand.customerId()))
          .restaurantId(new RestaurantId(createOrderCommand.restaurantId()))
          .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.address()))
          .price(new Money(createOrderCommand.price()))
          .items(orderItemsToOrderItemEntities(createOrderCommand.items()))
          .build();
  }

  public TrackOrderResponse orderToTrackOrderResponse(Order order) {
    return TrackOrderResponse.builder()
          .orderTrackingId(order.getTrackingId().getValue())
          .orderStatus(order.getOrderStatus())
          .build();
  }

  public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
    return CreateOrderResponse.builder()
          .orderTrackingId(order.getTrackingId().getValue())
          .orderStatus(order.getOrderStatus())
          .message(message)
          .build();
  }

  private List<OrderItem> orderItemsToOrderItemEntities(
        List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> orderItems
  ) {
    return orderItems.stream()
          .map(orderItem -> OrderItem.builder()
                .product(new Product(new ProductId(orderItem.productId())))
                .quantity(orderItem.quantity())
                .price(new Money(orderItem.price()))
                .subTotal(new Money(orderItem.subTotal()))
                .build())
          .toList();
  }


  private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
    return new StreetAddress(
          UUID.randomUUID(),
          orderAddress.street(),
          orderAddress.postalCode(),
          orderAddress.city()
    );
  }
}
