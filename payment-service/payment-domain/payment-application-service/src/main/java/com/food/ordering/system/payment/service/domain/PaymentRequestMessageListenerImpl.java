package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

  private final PaymentRequestHelper paymentRequestHelper;

  @Override
  public void completePayment(PaymentRequest paymentRequest) {
    PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);


    firePaymentEvent(paymentEvent);
  }

  @Override
  public void cancelPayment(PaymentRequest paymentRequest) {

    log.info("Payment cancelled : {}", paymentRequest);

    PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);

    log.info("Payment cancelled : {}", paymentEvent.getPayment());

    firePaymentEvent(paymentEvent);
  }

  private void firePaymentEvent(PaymentEvent paymentEvent) {

    log.info("Publish payment event with payment id: {} and order id: {}",
          paymentEvent.getPayment().getId().getValue(),
          paymentEvent.getPayment().getId().getValue());

    paymentEvent.fire();

  }
}
