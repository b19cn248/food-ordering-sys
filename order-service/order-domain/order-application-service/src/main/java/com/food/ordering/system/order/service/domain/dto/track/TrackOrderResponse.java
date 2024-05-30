package com.food.ordering.system.order.service.domain.dto.track;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record TrackOrderResponse(
      @NotNull
      UUID orderTrackingId,

      @NotNull
      OrderStatus orderStatus,

      @NotNull
      List<String> failureMessages

) {
}
