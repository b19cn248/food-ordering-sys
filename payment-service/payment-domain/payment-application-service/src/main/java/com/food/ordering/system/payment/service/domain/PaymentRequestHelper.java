package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestHelper {

  private final PaymentDomainService paymentDomainService;
  private final PaymentDataMapper paymentDataMapper;
  private final PaymentRepository paymentRepository;
  private final CreditEntryRepository creditEntryRepository;
  private final CreditHistoryRepository creditHistoryRepository;
  private final PaymentCompletedMessagePublisher paymentCompletedMessagePublisher;
  private final PaymentCancelledMessagePublisher paymentCancelledMessagePublisher;
  private final PaymentFailedMessagePublisher paymentFailedMessagePublisher;

  public PaymentEvent persistPayment(PaymentRequest paymentRequest) {

    log.info("Persisting payment for order id: {}", paymentRequest.getOrderId());

    Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);

    CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());

    List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());

    List<String> failureMessages = new ArrayList<>();

    PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(
          payment,
          creditEntry,
          creditHistories,
          failureMessages,
          paymentCompletedMessagePublisher,
          paymentFailedMessagePublisher
    );

    persistDbObjects(payment, failureMessages, creditEntry, creditHistories);

    return paymentEvent;
  }

  @Transactional
  public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
    log.info("Cancelling payment for order id: {}", paymentRequest.getOrderId());

    Optional<Payment> paymentOptional = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));

    if (paymentOptional.isEmpty()) {
      log.error("Payment not found for order id: {}", paymentRequest.getOrderId());

      throw new PaymentApplicationServiceException("Payment not found for order id: " + paymentRequest.getOrderId());
    }

    Payment payment = paymentOptional.get();

    log.info("Payment is when cancel: {}", payment);

    CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
    List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
    List<String> failureMessages = new ArrayList<>();

    PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(
          payment,
          creditEntry,
          creditHistories,
          failureMessages,
          paymentCancelledMessagePublisher,
          paymentFailedMessagePublisher
    );

    persistDbObjects(payment, failureMessages, creditEntry, creditHistories);

    return paymentEvent;
  }

  private void persistDbObjects(
        Payment payment,
        List<String> failureMessages,
        CreditEntry creditEntry,
        List<CreditHistory> creditHistories
  ) {
    paymentRepository.save(payment);

    if (failureMessages.isEmpty()) {
      creditEntryRepository.save(creditEntry);
      creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
    }
  }

  private CreditEntry getCreditEntry(CustomerId customerId) {

    Optional<CreditEntry> creditEntryOptional = creditEntryRepository.findByCustomerId(customerId);

    if (creditEntryOptional.isEmpty()) {
      log.error("Credit entry not found for customer id: {}", customerId.getValue());

      throw new PaymentApplicationServiceException("Credit entry not found for customer id: " + customerId.getValue());
    }

    return creditEntryOptional.get();
  }

  private List<CreditHistory> getCreditHistories(CustomerId customerId) {

    Optional<List<CreditHistory>> creditHistoriesOptional = creditHistoryRepository.findByCustomerId(customerId);

    if (creditHistoriesOptional.isEmpty()) {
      log.error("Credit histories not found for customer id: {}", customerId.getValue());

      throw new PaymentApplicationServiceException("Credit histories not found for customer id: " + customerId.getValue());
    }

    return creditHistoriesOptional.get();
  }

}
