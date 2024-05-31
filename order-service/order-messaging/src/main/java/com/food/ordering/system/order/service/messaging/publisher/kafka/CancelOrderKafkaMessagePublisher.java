package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCancelEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
  private final OrderKafkaMessageHelper orderKafkaMessageHelper;

  @Override
  public void publish(OrderCancelEvent event) {
    log.info("Publishing order cancel event to payment service");

    String orderId = event.getOrder().getId().getValue().toString();

    log.info("Order id: {}", orderId);

    try {
      PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.OrderCancelEventToPaymentRequestAvroModel(event);

      kafkaProducer.send(
            orderServiceConfigData.getPaymentRequestTopicName(),
            orderId,
            paymentRequestAvroModel,
            orderKafkaMessageHelper.getKafkaCallback(
                  orderServiceConfigData.getPaymentRequestTopicName(),
                  paymentRequestAvroModel,
                  orderId,
                  "PaymentRequestAvroModel"
            )
      );

      log.info("Order cancel event published to payment service");
    } catch (Exception e) {
      log.error("Error while publishing order cancel event to payment service with order id :{} and error :{}", orderId, e.getMessage());
    }
  }
}
