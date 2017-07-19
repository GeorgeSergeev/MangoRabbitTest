package ru.alvioneurope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by GSergeev on 13.07.2017.
 */
@Service
public class CallTrackingService {
    protected static Logger LOGGER = LoggerFactory.getLogger(CallTrackingService.class);
    @Autowired
    @Qualifier("crmConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Autowired
    @Qualifier ("crmAmqpAdmin")
    private RabbitAdmin rabbitAdmin;
    @Autowired
    @Qualifier ("replyQueue")
    private Queue replyQueue;

    private RabbitTemplate rabbitTemplate;
    private SimpleMessageListenerContainer replyListener;

    //@Autowired
    //private Queue replyQueue;
    //@Autowired
    //private RabbitTemplate rabbitTemplate;
    // private SimpleMessageListenerContainer replyListener;


    @PostConstruct
    public void init() {

        this.rabbitTemplate = new RabbitTemplate(this.connectionFactory);
        this.rabbitTemplate.setQueue(this.replyQueue.getName());
        this.rabbitTemplate.setReplyTimeout(60 * 1000);
        //this.rabbitTemplate.setReplyAddress(this.replyQueue.getName());
        DefaultMessagePropertiesConverter mc = new DefaultMessagePropertiesConverter();
        mc.setCorrelationIdPolicy(DefaultMessagePropertiesConverter.CorrelationIdPolicy.BOTH);
        this.rabbitTemplate.setMessagePropertiesConverter(mc);

        this.replyListener = new SimpleMessageListenerContainer(this.connectionFactory);
        this.replyListener.setRabbitAdmin(this.rabbitAdmin);
        this.replyListener.setQueues(this.replyQueue);
        this.replyListener.setMessageListener(new MessageListener()
        {
            @Override
            public void onMessage(Message message)
            {
                LOGGER.info("DCT INFO MESSAGE " + message);
            }
        });
    }

    public Optional<DCTInfo> getDCTUserInfoByDynamicNumber(String diversion) {
        if(!StringUtils.hasText(diversion)) {
            return Optional.empty();
        }
        try {
            StringBuilder sb = new StringBuilder(128);
            sb.append("[\"").append(diversion).append("\"]");
            LOGGER.info("DCT getUserInfoByDynamicNumber message: " + sb.toString());
            MessageProperties mp = new MessageProperties();
            mp.setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
            mp.setContentType("application/json");
            String correlationId = UUID.randomUUID().toString();
            //mp.setReplyTo(this.replyQueue.getName());
            mp.setCorrelationIdString(correlationId);
            Message msg = new Message(sb.toString().getBytes("UTF-8"), mp);
            Message reply = this.rabbitTemplate.sendAndReceive(
                    //this.rabbitTemplate.send(
                    "methods.call-tracking.api",
                    "methods.call-tracking.api.getUserInfoByDynamicNumber",
                    msg, new CorrelationData(correlationId));
            DCTInfo info = null;
            if (reply != null) {
                LOGGER.info("DCT getUserInfoByDynamicNumber reply: " + reply);
                if(reply.getBody() != null) {
                    String body = new String(reply.getBody(), "UTF-8");
                    if(StringUtils.hasText(body)) {
                        LOGGER.info("DCT getUserInfoByDynamicNumber reply body: " + body);
                        JsonParser parser = new JsonParser();
                        JsonElement json = parser.parse(body);
                        if(json instanceof JsonObject) {
                            JsonObject root = json.getAsJsonObject();
                            info = new DCTInfo();
                            info.setUid(root.get("uid").getAsString());
                            JsonElement ga_cid = root.get("ga_cid");
                            if(ga_cid instanceof JsonPrimitive) {
                                info.setCid(ga_cid.getAsString());
                            }
                            JsonObject location = root.get("location").getAsJsonObject();
                            JsonElement city = location.get("city");
                            if(city instanceof JsonPrimitive) {
                                info.setCity(city.getAsString());
                            }
                            JsonObject channel = root.get("channel").getAsJsonObject();
                            JsonElement source = channel.get("source");
                            if(source instanceof JsonPrimitive) {
                                info.setSource(source.getAsString());
                            }
                            JsonElement medium = channel.get("medium");
                            if(medium instanceof JsonPrimitive) {
                                info.setMedium(medium.getAsString());
                            }
                            JsonElement campaign = channel.get("campaign");
                            if(campaign instanceof JsonPrimitive) {
                                info.setCampaign(campaign.getAsString());
                            }
                            JsonElement content = channel.get("content");
                            if(content instanceof JsonPrimitive) {
                                info.setContent(content.getAsString());
                            }
                            JsonElement term = channel.get("term");
                            if(term instanceof JsonPrimitive) {
                                info.setTerm(term.getAsString());
                            }
                            JsonElement duration = root.get("duration");
                            if(duration instanceof JsonPrimitive) {
                                info.setDuration(duration.getAsInt());
                            }
                            JsonObject current_page = root.get("current_page").getAsJsonObject();
                            JsonElement url = current_page.get("url");
                            if(url instanceof JsonPrimitive) {
                                info.setCurrentPage(url.getAsString());
                            }
                        } else {
                            LOGGER.info("DCT getUserInfoByDynamicNumber - unknown diversity number");
                        }
                    } else {
                        LOGGER.info("DCT getUserInfoByDynamicNumber - empty response");
                    }
                }
            } else {
                LOGGER.info("DCT getUserInfoByDynamicNumber reply: NULL");
            }
            return Optional.ofNullable(info);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(null, e);
        }
        return null;
    }
}
