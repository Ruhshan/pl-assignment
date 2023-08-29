package xyz.ruhshan.pl.common.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private final CachingConnectionFactory cachingConnectionFactory;
    private final String batteryRegistrationQueue;
    private final String batteryRegistrationCompleteQueue;

    public RabbitMQConfig(CachingConnectionFactory cachingConnectionFactory,
        @Value("${queue.battery-registration}") String batteryRegistrationQueue,
        @Value("${queue.battery-registration-complete}") String batteryRegistrationCompleteQueue) {
        this.cachingConnectionFactory = cachingConnectionFactory;
        this.batteryRegistrationQueue = batteryRegistrationQueue;
        this.batteryRegistrationCompleteQueue = batteryRegistrationCompleteQueue;
    }

    @Bean
    public Queue createBatteryRegistrationQueue() {
        return new Queue(batteryRegistrationQueue);
    }

    @Bean
    public Queue createBatteryRegistrationCompletedQueue(){
        return new Queue(batteryRegistrationCompleteQueue);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

}
