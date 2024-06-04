package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderMessagingDataMapper {

  public PaymentRequestAvroModel OrderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
    Order order = orderCreatedEvent.getOrder();
    return PaymentRequestAvroModel.newBuilder()
          .setId(UUID.randomUUID().toString())
          .setSagaId("")
          .setOrderId(order.getId().getValue().toString())
          .setCustomerId(order.getCustomerId().getValue().toString())
          .setOrderId(order.getId().getValue().toString())
          .setPrice(order.getPrice().amount())
          .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
          .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
          .build();
  }

  public PaymentRequestAvroModel OrderCancelEventToPaymentRequestAvroModel(OrderCancelEvent orderCancelEvent) {
    Order order = orderCancelEvent.getOrder();
    return PaymentRequestAvroModel.newBuilder()
          .setOrderId(order.getId().getValue().toString())
          .setCustomerId(order.getCustomerId().getValue().toString())
          .setOrderId(order.getId().getValue().toString())
          .setPrice(order.getPrice().amount())
          .setCreatedAt(orderCancelEvent.getCreatedAt().toInstant())
          .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
          .build();
  }

  public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(OrderPaidEvent event) {
    Order order = event.getOrder();
    return RestaurantApprovalRequestAvroModel.newBuilder()
          .setId(order.getId().getValue().toString())
          .setSagaId("")
          .setOrderId(order.getId().getValue().toString())
          .setRestaurantId(order.getRestaurantId().getValue().toString())
          .setRestaurantOrderStatus(com.food.ordering.system.kafka.order.avro.model.RestaurantOrderStatus.
                valueOf(event.getOrder().getOrderStatus().name()))
          .setProducts(
                order.getItems().stream().map(orderItem ->
                      com.food.ordering.system.kafka.order.avro.model.Product.newBuilder()
                            .setId(orderItem.getProduct().getId().getValue().toString())
                            .setQuantity(orderItem.getQuantity())
                            .build()).toList()
          )
          .setPrice(order.getPrice().amount())
          .setCreatedAt(event.getCreatedAt().toInstant())
          .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
          .build();
  }

  public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel
                                                                         paymentResponseAvroModel) {
    return PaymentResponse.builder()
          .id(paymentResponseAvroModel.getId())
          .sagaId(paymentResponseAvroModel.getSagaId())
          .paymentId(paymentResponseAvroModel.getPaymentId())
          .customerId(paymentResponseAvroModel.getCustomerId())
          .orderId(paymentResponseAvroModel.getOrderId())
          .price(paymentResponseAvroModel.getPrice())
          .createdAt(paymentResponseAvroModel.getCreatedAt())
          .paymentStatus(com.food.ordering.system.domain.valueobject.PaymentStatus.valueOf(
                paymentResponseAvroModel.getPaymentStatus().name()))
          .failureMessages(paymentResponseAvroModel.getFailureMessages())
          .build();
  }


  public RestaurantApprovalResponse
  approvalResponseAvroModelToApprovalResponse(RestaurantApprovalResponseAvroModel
                                                    restaurantApprovalResponseAvroModel) {
    return RestaurantApprovalResponse.builder()
          .id(restaurantApprovalResponseAvroModel.getId())
          .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
          .restaurantId(restaurantApprovalResponseAvroModel.getRestaurantId())
          .orderId(restaurantApprovalResponseAvroModel.getOrderId())
          .createdAt(restaurantApprovalResponseAvroModel.getCreatedAt())
          .orderApprovalStatus(com.food.ordering.system.domain.valueobject.OrderApprovalStatus.valueOf(
                restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name()))
          .failureMessages(restaurantApprovalResponseAvroModel.getFailureMessages())
          .build();
  }
}
