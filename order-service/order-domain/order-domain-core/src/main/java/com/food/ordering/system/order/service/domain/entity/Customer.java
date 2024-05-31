package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregationRoot;
import com.food.ordering.system.domain.valueobject.CustomerId;

public class Customer extends AggregationRoot<CustomerId> {

  public Customer() {
  }

  public Customer(CustomerId id) {
    super.setId(id);
  }
}
