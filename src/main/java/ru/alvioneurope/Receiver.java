package ru.alvioneurope;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by GSergeev on 19.07.2017.
 */
@Component
public class Receiver {

    protected static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    private final static String HISTORY_QUEUE_NAME = "mango.itg.pbx.amqp.history.exchange";
    private final static String TRACKING_SERVICE_QUEUE_NAME = "mango.itg.pbx.amqp.call-tracking.exchange";
    final static String HISTORY_EXCHANGE_NAME ="events.vpbx.history";
    final static String TRACKING_SERVICE_EXCHANGE_NAME ="events.call-tracking-service.calls";


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = HISTORY_QUEUE_NAME, durable = "false"),
                    exchange = @Exchange(value = HISTORY_EXCHANGE_NAME, type = "fanout", durable = "true"),
                    key = "context.srv")
    )
    public void processHistory(Message message) {
        String messageBody= new String(message.getBody());
        LOGGER.info("History received  : "+messageBody);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = TRACKING_SERVICE_QUEUE_NAME, durable = "false"),
                    exchange = @Exchange(value = TRACKING_SERVICE_EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "#")
    )
    public void processTracking(Message message) {
        String messageBody= new String(message.getBody());
        LOGGER.info("Tracking received : "+messageBody);
    }

}
