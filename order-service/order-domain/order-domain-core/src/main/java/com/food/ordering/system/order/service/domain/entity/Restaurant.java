package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregationRoot;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class Restaurant extends AggregationRoot<RestaurantId> {

  private final List<Product> products;
  private boolean active;

}
