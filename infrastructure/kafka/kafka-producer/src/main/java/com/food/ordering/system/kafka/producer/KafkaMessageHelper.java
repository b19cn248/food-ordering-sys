package com.food.ordering.system.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaMessageHelper {

  public <T> CompletableFuture<SendResult<String, T>> getKafkaCallback(
        String paymentRequestTopicName,
        T avroModel,
        String orderId,
        String avroModelName
  ) {
    CompletableFuture<SendResult<String, T>> callback = new CompletableFuture<>();

    callback.whenComplete((result, ex) -> {
      if (ex != null) {
        log.error("Error while sending " + avroModelName + " PaymentRequestAvroModel message {} to topic: {}",
              avroModel.toString(), paymentRequestTopicName, ex);
      } else {
        RecordMetadata recordMetadata = result.getRecordMetadata();
        log.info("Received successful response from Kafka for order id: {}, Topic:{}, Partition:{}, Offset:{}, Timestamp:{}",
              orderId,
              recordMetadata.topic(),
              recordMetadata.partition(),
              recordMetadata.offset(),
              recordMetadata.timestamp()
        );
      }
    });

    return callback;
  }
}
