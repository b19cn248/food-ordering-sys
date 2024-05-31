package com.food.ordering.system.order.service.dataaccess.order.adapter;

import com.food.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.order.repository.OrderJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderJpaRepository orderJpaRepository;
  private final OrderDataAccessMapper orderDataAccessMapper;

  @Override
  public Order save(Order order) {
    log.info("Saving order with trackingId={}", order.getTrackingId());

    return orderDataAccessMapper.mapToDomain(orderJpaRepository.save(orderDataAccessMapper.mapToEntity(order)));
  }

  @Override
  public Optional<Order> findByTrackingNumber(TrackingId trackingId) {
    log.info("Finding order by trackingId={}", trackingId);

    return orderJpaRepository.findByTrackingId(trackingId.getValue())
          .map(orderDataAccessMapper::mapToDomain);
  }
}
