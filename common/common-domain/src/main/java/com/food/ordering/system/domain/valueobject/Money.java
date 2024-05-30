package com.food.ordering.system.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {

  public static final Money ZERO = new Money(BigDecimal.ZERO);

  public Money {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    amount = setScale(amount);
  }

  public boolean isGreaterThanZero() {
    return amount.compareTo(BigDecimal.ZERO) > 0;
  }

  public boolean isGreaterThan(Money money) {
    return amount.compareTo(money.amount) > 0;
  }

  public Money add(Money money) {
    return new Money(this.setScale(this.amount.add(money.amount)));
  }

  public Money subtract(Money money) {
    return new Money(this.setScale(this.amount.subtract(money.amount)));
  }

  public Money multiply(int value) {
    return new Money(this.setScale(this.amount.multiply(BigDecimal.valueOf(value))));
  }

  private BigDecimal setScale(BigDecimal value) {
    return value.setScale(2, RoundingMode.HALF_EVEN);
  }
}
