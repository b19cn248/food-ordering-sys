package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentCompletedEvent extends PaymentEvent {

  private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;

  public PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages,
                               DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher) {
    super(payment, createdAt, failureMessages);
    this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
  }

  @Override
  public void fire() {
    paymentCompletedEventDomainEventPublisher.publish(this);
  }
}
