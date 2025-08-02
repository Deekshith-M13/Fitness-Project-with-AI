package com.fm.activityservice.Config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



//Everything should be imported from amqq
@Configuration
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.queue.name}")
    private String queue;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchange;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    //    import Queue from amqp
    @Bean
    public Queue activitQueue() {
        return new Queue(queue , true);
  }
//  this is to create a queue in rabbitmq of name and true is to not loose the queue when the rabittmq is restarted

    @Bean
    public DirectExchange  directExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding activityBinding(Queue activityQueue,DirectExchange directExchange) {
        return BindingBuilder.bind(activityQueue).to(directExchange).with(routingKey);

    }
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
//    converts java objects to json
}
