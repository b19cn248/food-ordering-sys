package com.food.ordering.system.restaurant.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApprovedKafkaMessagePublisher implements OrderApprovedMessagePublisher {

  private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
  private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
  private final RestaurantServiceConfigData restaurantServiceConfigData;
  private final KafkaMessageHelper kafkaMessageHelper;

  @Override
  public void publish(OrderApprovedEvent event) {
    String orderId = event.getOrderApproval().getOrderId().getValue().toString();

    log.info("Received OrderApprovedEvent for order id: {}", orderId);

    try {
      RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel = restaurantMessagingDataMapper
            .orderApprovedEventToRestaurantApprovalResponseAvroModel(event);

      kafkaProducer.send(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
            orderId,
            restaurantApprovalResponseAvroModel,
            kafkaMessageHelper.getKafkaCallback(
                  restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                  restaurantApprovalResponseAvroModel,
                  orderId,
                  "RestaurantApprovalResponseAvroModel"
            )
      );

      log.info("RestaurantApprovalResponseAvroModel sent for order id: {}", orderId);
    } catch (Exception e) {
      log.error("Error while sending RestaurantApprovalResponseAvroModel for order id: {}, error : {}", orderId, e.getMessage());
    }
  }
}
