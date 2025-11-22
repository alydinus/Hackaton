package kg.aiu.telegram_sevrice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {
    public static final String ORDER_QUEUE = "order.queue";
    public static final String PRODUCT_QUEUE = "product.queue";
    public static final String CRM_EXCHANGE = "crm.exchange";
    public static final String ORDER_ROUTING_KEY = "order.created";
    public static final String PRODUCT_ROUTING_KEY = "product.created";


    public static final String RPC_ORDER_QUEUE = "rpc.order.requests";
    public static final String RPC_PRODUCT_QUEUE = "rpc.product.requests";
    public static final String RPC_ORDERS_LIST_QUEUE = "rpc.orders.list.requests";

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public Queue productQueue() {
        return new Queue(PRODUCT_QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(CRM_EXCHANGE);
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, TopicExchange exchange) {
        return BindingBuilder.bind(orderQueue)
                .to(exchange)
                .with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding productBinding(Queue productQueue, TopicExchange exchange) {
        return BindingBuilder.bind(productQueue)
                .to(exchange)
                .with(PRODUCT_ROUTING_KEY);
    }

    public static final String RPC_EXCHANGE = "rpc.exchange";

    @Bean
    public Queue rpcOrderQueue() {
        return new Queue(RPC_ORDER_QUEUE);
    }

    @Bean
    public Queue rpcProductQueue() {
        return new Queue(RPC_PRODUCT_QUEUE);
    }

    @Bean
    public Queue rpcOrdersListQueue() {
        return new Queue(RPC_ORDERS_LIST_QUEUE);
    }

    @Bean
    public DirectExchange rpcExchange() {
        return new DirectExchange(RPC_EXCHANGE);
    }

    @Bean
    public Binding rpcOrderBinding(Queue rpcOrderQueue, DirectExchange exchange) {
        return BindingBuilder.bind(rpcOrderQueue).to(exchange).with(RPC_ORDER_QUEUE);
    }

    @Bean
    public Binding rpcProductBinding(Queue rpcProductQueue, DirectExchange exchange) {
        return BindingBuilder.bind(rpcProductQueue).to(exchange).with(RPC_PRODUCT_QUEUE);
    }

    @Bean
    public Binding rpcOrdersListBinding(Queue rpcOrdersListQueue, DirectExchange exchange) {
        return BindingBuilder.bind(rpcOrdersListQueue).to(exchange).with(RPC_ORDERS_LIST_QUEUE);
    }
}
