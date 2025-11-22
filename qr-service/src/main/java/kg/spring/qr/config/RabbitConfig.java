package kg.spring.qr.config;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitConfig {

    @Value("${exchange}")
    private String exchangeName;

    @Value("${queues.order-created}")
    private String orderCreatedQueueName;

    @Value("${queues.qr-generated}")
    private String qrGeneratedQueueName;

    @Bean
    public TopicExchange crmExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(orderCreatedQueueName).build();
    }

    @Bean
    public Queue qrGeneratedQueue() {
        return QueueBuilder.durable(qrGeneratedQueueName).build();
    }

    @Bean
    public Binding bindOrderCreated(Queue orderCreatedQueue, TopicExchange crmExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(crmExchange).with(orderCreatedQueueName);
    }

    @Bean
    public Binding bindQrGenerated(Queue qrGeneratedQueue, TopicExchange crmExchange) {
        return BindingBuilder.bind(qrGeneratedQueue).to(crmExchange).with(qrGeneratedQueueName);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}