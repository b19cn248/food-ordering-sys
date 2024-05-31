package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Component
public class OrderDataAccessMapper {

  public OrderEntity mapToEntity(Order order) {
    OrderEntity orderEntity = OrderEntity.builder()
          .id(order.getId().getValue())
          .customerId(order.getCustomerId().getValue())
          .restaurantId(order.getRestaurantId().getValue())
          .trackingId(order.getTrackingId().getValue())
          .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
          .price(order.getPrice().amount())
          .items(orderItemsToOrderItemEntities(order.getItems()))
          .orderStatus(order.getOrderStatus())
          .failureMessages(order.getFailureMessages() != null ?
                String.join(Order.FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : "")
          .build();

    orderEntity.getAddress().setOrder(orderEntity);

    orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));

    return orderEntity;
  }


  public Order mapToDomain(OrderEntity orderEntity) {

    Order order = Order.builder()
          .customerId(new CustomerId(orderEntity.getCustomerId()))
          .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
          .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
          .price(new Money(orderEntity.getPrice()))
          .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
          .trackingId(new TrackingId(orderEntity.getTrackingId()))
          .orderStatus(orderEntity.getOrderStatus())
          .failureMessages(orderEntity.getFailureMessages().isEmpty() ? new ArrayList<>() :
                new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages()
                      .split(FAILURE_MESSAGE_DELIMITER))))
          .build();

    order.setId(new OrderId(orderEntity.getId()));

    return order;
  }

  private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {

    return items.stream()
          .map(this::orderItemEntityToOrderItem)
          .toList();
  }

  private OrderItem orderItemEntityToOrderItem(OrderItemEntity orderItemEntity) {

    OrderItem orderItem = OrderItem.builder()
          .product(new Product(new ProductId(orderItemEntity.getProductId())))
          .quantity(orderItemEntity.getQuantity())
          .subTotal(new Money(orderItemEntity.getSubTotal()))
          .price(new Money(orderItemEntity.getPrice()))
          .build();

    orderItem.setId(new OrderItemId(orderItemEntity.getId()));

    return orderItem;
  }

  private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {

    return StreetAddress.builder()
          .street(address.getStreet())
          .city(address.getCity())
          .postalCode(address.getPostalCode())
          .build();
  }

  private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {

    return items.stream()
          .map(this::orderItemToOrderItemEntity)
          .toList();
  }

  private OrderItemEntity orderItemToOrderItemEntity(OrderItem orderItem) {

    return OrderItemEntity.builder()
          .id(orderItem.getId().getValue())
          .productId(orderItem.getProduct().getId().getValue())
          .quantity(orderItem.getQuantity())
          .subTotal(orderItem.getSubTotal().amount())
          .price(orderItem.getPrice().amount())
          .build();
  }

  private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {

    return OrderAddressEntity.builder()
          .street(deliveryAddress.street())
          .city(deliveryAddress.city())
          .postalCode(deliveryAddress.postalCode())
          .build();
  }

}
