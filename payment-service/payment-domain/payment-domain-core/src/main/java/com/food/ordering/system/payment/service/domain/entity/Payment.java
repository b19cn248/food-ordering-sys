package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregationRoot;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.payment.service.domain.valueobject.PaymentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class Payment extends AggregationRoot<PaymentId> {
  private final OrderId orderId;
  private final CustomerId customerId;

  private final Money price;

  private PaymentStatus paymentStatus;

  private ZonedDateTime createdAt;

  public void initializePayment() {
    setId(new PaymentId(UUID.randomUUID()));
    createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
  }

  public void validatePayment(List<String> failureMessages) {
    if (Objects.isNull(price) || !price.isGreaterThanZero()) {
      failureMessages.add("Price must be greater than zero");
    }
  }

  public void updateStatus(PaymentStatus paymentStatus) {
    this.paymentStatus = paymentStatus;
  }
}
