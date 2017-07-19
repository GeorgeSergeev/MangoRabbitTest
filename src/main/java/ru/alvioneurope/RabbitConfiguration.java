package ru.alvioneurope;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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
    final static String replyQueueName = "mango.itg.pbx.amqp.call-tracking.reply";
    final static String eventQueueName = "mango.itg.pbx.amqp.call-tracking.exchange";
    final static String exchangeName ="events.call-tracking-service.calls";
    final static String routingKey ="dynamic";

    @Bean (name = "crmConnectionFactory")
    public ConnectionFactory connectionFactory() {
        logger.info("Connect to rabbit-bus-prod.as.ru.mgo.su");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("rabbit-bus-prod.as.ru.mgo.su");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean (name = "crmAmqpAdmin")
    public RabbitAdmin amqpAdmin() {
        logger.info("Rabbit Admin has been created");
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        return rabbitAdmin;
    }

    @Bean (name = "replyQueue")
    public  Queue replyQueue() {
        logger.info("Reply Queue has been created");
        return new Queue(replyQueueName,false);
    }


    @Bean (name = "eventQueue")
    public Queue eventQueue() {
        logger.info("Event Queue has been created");
        return new Queue(eventQueueName,false);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(exchangeName);
    }


    @Bean
    public Binding dynamicBinding() {
        logger.info("Binding dynamic has been created");
        return BindingBuilder.bind(eventQueue()).to(topicExchange()).with("dynamic");
    }

    @Bean
    public Binding staticBinding() {
        logger.info("Binding static has been created");
        return BindingBuilder.bind(eventQueue()).to(topicExchange()).with("static");
    }

    @Bean
    public Binding defaultBinding() {
        logger.info("Binding default has been created");
        return BindingBuilder.bind(eventQueue()).to(topicExchange()).with("default");
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(eventQueueName);
        container.setMessageListener(new MessageListenerAdapter(new MessageHandler()));
        return container;
    }
/*
    @Bean(name="rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory listenerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }
*/
}
