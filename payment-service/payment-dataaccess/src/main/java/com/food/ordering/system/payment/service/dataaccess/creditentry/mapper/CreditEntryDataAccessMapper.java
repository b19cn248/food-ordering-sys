package com.food.ordering.system.payment.service.dataaccess.creditentry.mapper;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {

  public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
    CreditEntry creditEntry = CreditEntry.builder()
          .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
          .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
          .build();

    creditEntry.setId(new CreditEntryId(creditEntryEntity.getId()));

    return creditEntry;
  }

  public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
    return CreditEntryEntity.builder()
          .id(creditEntry.getId().getValue())
          .customerId(creditEntry.getCustomerId().getValue())
          .totalCreditAmount(creditEntry.getTotalCreditAmount().amount())
          .build();
  }

}
