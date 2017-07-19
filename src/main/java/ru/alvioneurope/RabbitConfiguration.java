package ru.alvioneurope;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by GSergeev on 13.07.2017.
 */


@EnableRabbit
@ComponentScan("ru.alvioneurope")
@Configuration
public class RabbitConfiguration {

    Logger logger = Logger.getLogger(RabbitConfiguration.class);
    final static String queueName = "mango.itg.pbx.amqp.call-tracking.exchange";
    final static String exchangeName ="events.call-tracking-service.calls";

    @Autowired
    Receiver receiver;

    @Bean (name = "crmConnectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("rabbit-bus-prod.as.ru.mgo.su");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean (name = "crmAmqpAdmin")
    public RabbitAdmin amqpAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        return rabbitAdmin;
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queueName);
    }

    @Bean
    SimpleMessageListenerContainer container() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        Queue eventsQueue = queue();
        TopicExchange topicExchange = exchange();
        container.setConnectionFactory(connectionFactory());
        container.setQueues(eventsQueue);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter(receiver));
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        Binding bind= binding(eventsQueue,topicExchange);

        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
