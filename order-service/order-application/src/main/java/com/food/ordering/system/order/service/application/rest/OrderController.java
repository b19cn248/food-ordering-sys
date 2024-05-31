package com.food.ordering.system.order.service.application.rest;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/order", produces = "application/vnd.api.v1+json")
public class OrderController {

  private final OrderApplicationService orderApplicationService;

  public OrderController(OrderApplicationService orderApplicationService) {
    this.orderApplicationService = orderApplicationService;
  }

  @PostMapping
  public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {

    log.info("Received create order request for customer id: {}", createOrderCommand.customerId());
    CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
    log.info("Order created successfully for tracking id: {}", createOrderResponse.getOrderTrackingId());

    return ResponseEntity.ok(createOrderResponse);
  }

  @GetMapping("/{orderTrackingId}")
  public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID orderTrackingId) {
    log.info("Received get order by tracking id request for tracking id: {}", orderTrackingId);
    TrackOrderResponse trackOrderResponse = orderApplicationService.trackOrder(
          TrackOrderQuery.builder().orderTrackingId(orderTrackingId).build()
    );
    log.info("Order found for tracking id: {}", orderTrackingId);
    return ResponseEntity.ok(trackOrderResponse);
  }
}
