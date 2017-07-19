package ru.alvioneurope;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Created by GSergeev on 19.07.2017.
 */
@Component
public class Receiver {

    protected static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    private final static String QUEUE_NAME = "mango.itg.pbx.amqp.call-tracking.exchange";
    final static String EXCHANGE_NAME ="events.vpbx.history";
/*
    @RabbitListener(queues = QUEUE_NAME)
    public void onMessage(String message) {
        LOGGER.info("Yehoo!!! Accepted : " + message);
    }
*/

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = QUEUE_NAME, durable = "false"),
                    exchange = @Exchange(value = EXCHANGE_NAME, type = "fanout", durable = "true"),
                    key = "context.srv")
    )
    public void processOrder(Message message) {
        String messageBody= new String(message.getBody());
        System.out.println("Received : "+messageBody);
    }
}
