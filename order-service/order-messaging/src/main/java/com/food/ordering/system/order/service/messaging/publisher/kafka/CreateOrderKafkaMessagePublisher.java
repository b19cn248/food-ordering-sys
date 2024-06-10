package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

  private final OrderMessagingDataMapper orderMessagingDataMapper;
  private final OrderServiceConfigData orderServiceConfigData;
  private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
  private final KafkaMessageHelper orderKafkaMessageHelper;

  @Override
  public void publish(OrderCreatedEvent event) {
    log.info("Publishing order created event to payment service");

    String orderId = event.getOrder().getId().getValue().toString();

    log.info("Order id: {}", orderId);
    log.info("Topic name: {}", orderServiceConfigData.getPaymentRequestTopicName());


    try {
      PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.OrderCreatedEventToPaymentRequestAvroModel(event);


      log.info("PaymentRequestAvroModel: {}", paymentRequestAvroModel);

      log.info("PaymentRequestAvroModel: {}", paymentRequestAvroModel);

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

      log.info("Order created event published to payment service");
    } catch (Exception e) {
      log.error("Error while publishing order created event to payment service with order id :{} and error :{}", orderId, e.getMessage());
    }
  }

}
