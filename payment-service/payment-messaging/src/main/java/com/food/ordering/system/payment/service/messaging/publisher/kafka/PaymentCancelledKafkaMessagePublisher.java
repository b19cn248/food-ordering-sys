package com.food.ordering.system.payment.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentCancelledKafkaMessagePublisher implements PaymentCancelledMessagePublisher {

  private final PaymentMessagingDataMapper paymentMessagingDataMapper;
  private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
  private final PaymentServiceConfigData paymentServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  @Override
  public void publish(PaymentCancelledEvent event) {
    String orderId = event.getPayment().getOrderId().getValue().toString();

    log.info("Received PaymentCancelledEvent for order id: {}", orderId);

    try {
      PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper
            .paymentCancelledEventToPaymentResponseAvroModel(event);

      kafkaProducer.send(
            paymentServiceConfigData.getPaymentResponseTopicName(),
            orderId,
            paymentResponseAvroModel,
            kafkaMessageHelper.getKafkaCallback(
                  paymentServiceConfigData.getPaymentResponseTopicName(),
                  paymentResponseAvroModel,
                  orderId,
                  "PaymentResponseAvroModel"
            )
      );

      log.info("PaymentResponseAvroModel sent to kafka for order id: {}", orderId);
    } catch (Exception e) {
      log.error("Error while sending PaymentResponseAvroModel message to Kafka with order id : {}, error:{}", orderId, e.getMessage());
    }
  }
}
