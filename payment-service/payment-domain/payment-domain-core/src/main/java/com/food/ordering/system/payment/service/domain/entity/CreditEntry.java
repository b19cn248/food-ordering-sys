package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.payment.service.domain.valueobject.CreditEntryId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
public class CreditEntry extends BaseEntity<CreditEntryId> {

  private final CustomerId customerId;
  private Money totalCreditAmount;

  public void addCreditAmount(Money creditAmount) {
    totalCreditAmount = totalCreditAmount.add(creditAmount);
  }

  public void subtractCreditAmount(Money creditAmount) {
    totalCreditAmount = totalCreditAmount.subtract(creditAmount);
  }
}
