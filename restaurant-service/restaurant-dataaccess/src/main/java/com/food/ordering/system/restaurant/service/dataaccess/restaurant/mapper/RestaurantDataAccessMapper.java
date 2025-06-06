package com.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper;

import com.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.dataaccess.restaurant.entity.OrderApprovalEntity;
import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval;
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

  public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
    return restaurant.getOrderDetail().getProducts().stream()
          .map(product -> product.getId().getValue())
          .collect(Collectors.toList());
  }

  public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
    RestaurantEntity restaurantEntity =
          restaurantEntities.stream().findFirst().orElseThrow(() ->
                new RestaurantDataAccessException("No restaurants found!"));

    List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                Product.builder()
                      .productId(new ProductId(entity.getProductId()))
                      .name(entity.getProductName())
                      .price(new Money(entity.getProductPrice()))
                      .available(entity.getProductAvailable())
                      .build())
          .collect(Collectors.toList());

    Restaurant restaurant = Restaurant.builder()
          .orderDetail(OrderDetail.builder()
                .products(restaurantProducts)
                .build())
          .active(restaurantEntity.getRestaurantActive())
          .build();

    restaurant.setId(new RestaurantId(restaurantEntity.getRestaurantId()));

    return restaurant;
  }

  public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval orderApproval) {
    return OrderApprovalEntity.builder()
          .id(orderApproval.getId().getValue())
          .restaurantId(orderApproval.getRestaurantId().getValue())
          .orderId(orderApproval.getOrderId().getValue())
          .status(orderApproval.getApprovalStatus())
          .build();
  }

  public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity orderApprovalEntity) {
    OrderApproval orderApproval = OrderApproval.builder()
          .restaurantId(new RestaurantId(orderApprovalEntity.getRestaurantId()))
          .orderId(new OrderId(orderApprovalEntity.getOrderId()))
          .approvalStatus(orderApprovalEntity.getStatus())
          .build();

    orderApproval.setId(new OrderApprovalId(orderApprovalEntity.getId()));
    return orderApproval;
  }

}
