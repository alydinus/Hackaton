package kg.shoro.crm.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitConfig {

    @Value("${crm.exchange}")
    private String exchangeName;

    @Value("${crm.routing.order-created}")
    private String orderCreatedRoutingKey;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TopicExchange crmExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(orderCreatedRoutingKey).build();
    }

    @Bean
    public Binding bindOrderCreated(Queue orderCreatedQueue, TopicExchange crmExchange) {
        return BindingBuilder
                .bind(orderCreatedQueue)
                .to(crmExchange)
                .with(orderCreatedRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter,
                                         RabbitTemplateConfigurer configurer) {

        RabbitTemplate template = new RabbitTemplate();
        template.setMessageConverter(messageConverter);
        configurer.configure(template, connectionFactory);
        return template;
    }
}