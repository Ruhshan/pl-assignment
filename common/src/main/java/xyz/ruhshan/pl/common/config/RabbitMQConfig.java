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

    public RabbitMQConfig(CachingConnectionFactory cachingConnectionFactory,
        @Value("${queue.battery-registration}") String batteryRegistrationQueue) {
        this.cachingConnectionFactory = cachingConnectionFactory;
        this.batteryRegistrationQueue = batteryRegistrationQueue;
    }

    @Bean
    public Queue createUserRegistrationQueue() {
        return new Queue(batteryRegistrationQueue);
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
