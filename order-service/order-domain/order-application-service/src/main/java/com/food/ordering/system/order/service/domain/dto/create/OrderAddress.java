package com.food.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderAddress(

      @NotNull
      @Max(value = 50)
      String street,

      @NotNull
      @Max(value = 10)
      String postalCode,

      @NotNull
      @Max(value = 50)
      String city
) {
}
