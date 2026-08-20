package vinix.kafka;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import vinix.services.exceptions.ResourceNotFoundException;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {

        var recoverer = new DeadLetterPublishingRecoverer(template,
            (record, ex) ->
                new TopicPartition(record.topic() + ".DLT", record.partition()));

        // 3 tentativas, com 1s de intervalo entre elas
        var backOff = new FixedBackOff(1000L, 3L);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
        handler.addNotRetryableExceptions(ResourceNotFoundException.class);

        return handler;
    }
}