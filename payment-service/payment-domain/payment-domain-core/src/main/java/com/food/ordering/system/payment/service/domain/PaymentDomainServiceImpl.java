package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.UTC;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {
    @Override
    public PaymentEvent validateAndInitiatePayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher
    ) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditAmount(payment, creditEntry);
        updateCreditHistories(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistories(creditEntry, creditHistories, failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Payment is initiated for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(
                    payment,
                    ZonedDateTime.now(ZoneId.of(UTC)),
                    failureMessages,
                    paymentCompletedEventDomainEventPublisher
            );
        } else {
            log.info("Payment initiation is failed for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(
                    payment,
                    ZonedDateTime.now(ZoneId.of(UTC)),
                    failureMessages,
                    paymentFailedEventDomainEventPublisher
            );
        }

    }

    @Override
    public PaymentEvent validateAndCancelPayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher
    ) {
        payment.validatePayment(failureMessages);
        addCreditAmount(payment, creditEntry);
        updateCreditHistories(payment, creditHistories, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment is cancelled for order id: {}", payment.getOrderId().getValue());
            return new PaymentCancelledEvent(
                    payment,
                    ZonedDateTime.now(ZoneId.of(UTC)),
                    failureMessages,
                    paymentCancelledEventDomainEventPublisher
            );
        } else {
            log.info("Payment cancellation is failed for order id: {}", payment.getOrderId().getValue());
            return new PaymentFailedEvent(
                    payment,
                    ZonedDateTime.now(ZoneId.of(UTC)),
                    failureMessages,
                    paymentFailedEventDomainEventPublisher
            );
        }
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {

        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            log.error("Customer with id: {} does not have enough credit to pay for the order", creditEntry.getCustomerId().getValue());

            failureMessages.add("Customer with id" + payment.getCustomerId().getValue()
                    + " does not have enough credit to pay for the order");
        }
    }

    private void subtractCreditAmount(Payment payment, CreditEntry creditEntry) {

        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void updateCreditHistories(Payment payment, List<CreditHistory> creditHistories, TransactionType transactionType) {

        CreditHistory creditHistory = CreditHistory.builder()
                .customerId(payment.getCustomerId())
                .amount(payment.getPrice())
                .transactionType(transactionType)
                .build();
        creditHistory.setId(new CreditHistoryId(UUID.randomUUID()));
        creditHistories.add(creditHistory);
    }

    private void validateCreditHistories(
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages) {

        Money totalCreditHistory = this.getTotalHistoryAmount(creditHistories, TransactionType.CREDIT);
        Money totalDebitHistory = this.getTotalHistoryAmount(creditHistories, TransactionType.DEBIT);

        if (totalDebitHistory.isGreaterThan(totalCreditHistory)) {
            log.error("Customer with id: {} doesn't have enough credit according to credit history",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add("Customer with id=" + creditEntry.getCustomerId().getValue() +
                    " doesn't have enough credit according to credit history!");
        }

        if (!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))) {
            log.error("Credit history total is not equal to current credit for customer id: {}!",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add("Credit history total is not equal to current credit for customer id: " +
                    creditEntry.getCustomerId().getValue() + "!");
        }
    }

    private Money getTotalHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {
        return creditHistories.stream()
                .filter(creditHistory -> creditHistory.getTransactionType() == transactionType)
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    private void addCreditAmount(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }

}
