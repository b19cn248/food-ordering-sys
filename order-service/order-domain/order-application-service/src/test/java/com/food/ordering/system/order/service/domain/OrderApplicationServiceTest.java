package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OrderTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderApplicationServiceTest {

  @Autowired
  private OrderApplicationService orderApplicationService;

  @Autowired
  private OrderDataMapper orderDataMapper;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private RestaurantRepository restaurantRepository;

  private CreateOrderCommand createOrderCommand;
  private CreateOrderCommand createOrderCommandWrongPrice;
  private CreateOrderCommand createOrderCommandWrongProductPrice;

  private final UUID CUSTOMER_ID = UUID.fromString("f4b3b9b1-5e8a-4d9f-9d9f-6e0b7d9b8d9b");
  private final UUID RESTAURANT_ID = UUID.fromString("010c651c-8f23-4796-8257-f730f67755d0");
  private final UUID PRODUCT_ID = UUID.fromString("2f79698d-31ad-4c13-a1ff-85aa1e020b41");
  private final UUID ORDER_ID = UUID.fromString("93087ffb-61c9-4b49-91b3-b2e95e9c09d6");
  private final BigDecimal PRICE = new BigDecimal("200.00");


  @BeforeAll
  public void init() {
    createOrderCommand = CreateOrderCommand.builder()
          .customerId(CUSTOMER_ID)
          .restaurantId(RESTAURANT_ID)
          .address(OrderAddress.builder()
                .street("street_1")
                .postalCode("1000AB")
                .city("Paris")
                .build())
          .price(PRICE)
          .items(List.of(OrderItem.builder()
                      .productId(PRODUCT_ID)
                      .quantity(1)
                      .price(new BigDecimal("50.00"))
                      .subTotal(new BigDecimal("50.00"))
                      .build(),
                OrderItem.builder()
                      .productId(PRODUCT_ID)
                      .quantity(3)
                      .price(new BigDecimal("50.00"))
                      .subTotal(new BigDecimal("150.00"))
                      .build()))
          .build();

    createOrderCommandWrongPrice = CreateOrderCommand.builder()
          .customerId(CUSTOMER_ID)
          .restaurantId(RESTAURANT_ID)
          .address(OrderAddress.builder()
                .street("street_1")
                .postalCode("1000AB")
                .city("Paris")
                .build())
          .price(new BigDecimal("250.00"))
          .items(List.of(OrderItem.builder()
                      .productId(PRODUCT_ID)
                      .quantity(1)
                      .price(new BigDecimal("50.00"))
                      .subTotal(new BigDecimal("50.00"))
                      .build(),
                OrderItem.builder()
                      .productId(PRODUCT_ID)
                      .quantity(3)
                      .price(new BigDecimal("50.00"))
                      .subTotal(new BigDecimal("150.00"))
                      .build()))
          .build();

    createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
          .customerId(CUSTOMER_ID)
          .restaurantId(RESTAURANT_ID)
          .address(OrderAddress.builder()
                .street("street_1")
                .postalCode("1000AB")
                .city("Paris")
                .build())
          .price(new BigDecimal("210.00"))
          .items(List.of(OrderItem.builder()
                      .productId(PRODUCT_ID)
                      .quantity(1)
                      .price(new BigDecimal("60.00"))
                      .subTotal(new BigDecimal("60.00"))
                      .build(),
                OrderItem.builder()
                      .productId(PRODUCT_ID)
                      .quantity(3)
                      .price(new BigDecimal("50.00"))
                      .subTotal(new BigDecimal("150.00"))
                      .build()))
          .build();


    Customer customer = new Customer();
    customer.setId(new CustomerId(CUSTOMER_ID));

    Restaurant restaurant = Restaurant.builder()
          .products(List.of(
                new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
          ))
          .active(true)
          .build();
    restaurant.setId(new RestaurantId(RESTAURANT_ID));


    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    order.setId(new OrderId(ORDER_ID));


    when(customerRepository.findCustomer(any())).thenReturn(Optional.of(customer));
    when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
          .thenReturn(Optional.of(restaurant));
    when(orderRepository.save(any())).thenReturn(order);
  }


  @Test
  void testCreateOrder() {
    CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
    assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
    assertEquals("Order created successfully.", createOrderResponse.getMessage());
    assertNotNull(createOrderResponse.getOrderTrackingId());
  }

  @Test
  void testCreateOrderWithWrongPrice() {

    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
          () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));

    assertEquals("Total price: 250.00 is not equal to the sum of items price: 200.00", orderDomainException.getMessage());
  }

  @Test
  void testCreateOrderWithWrongProductPrice() {

    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
          () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));

    assertEquals("Order item price 60.00 is not valid for product: 2f79698d-31ad-4c13-a1ff-85aa1e020b41", orderDomainException.getMessage());
  }

  @Test
  void testCreateOrderWithPassiveRestaurant() {
    Restaurant restaurant = Restaurant.builder()
          .products(List.of(
                new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
          ))
          .active(false)
          .build();

    restaurant.setId(new RestaurantId(RESTAURANT_ID));

    when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
          .thenReturn(Optional.of(restaurant));

    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
          () -> orderApplicationService.createOrder(createOrderCommand));

    assertEquals("Restaurant with id: 010c651c-8f23-4796-8257-f730f67755d0 is not active", orderDomainException.getMessage());


  }
}
