package org.elmifdali.transactionservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {

    private static final String TOPIC = "bank.transactions.created";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    @Bean
    NewTopic transactionsCreatedTopic() {
        return TopicBuilder.name(TOPIC).partitions(3).replicas(1).build();
    }

    // After commit only: a transaction that is rolled back never publishes an event.
    // The transaction is already saved here, so a Kafka failure must not fail the request.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(TransactionCreatedEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event.accountId().toString(), jsonMapper.writeValueAsString(event))
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Event {} not published", event.eventId(), error);
                        }
                    });
        } catch (RuntimeException e) {
            log.error("Event {} not published", event.eventId(), e);
        }
    }
}
