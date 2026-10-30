package org.elmifdali.transactionconsumerservice;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// Only logs the event: this service has no database and never changes banking data
@Component
public class TransactionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventConsumer.class);

    @KafkaListener(topics = "bank.transactions.created")
    public void onTransactionCreated(ConsumerRecord<String, String> event) {
        log.info("Event received: key={} partition={} offset={} value={}",
                event.key(), event.partition(), event.offset(), event.value());
    }
}
