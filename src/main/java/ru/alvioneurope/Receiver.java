package ru.alvioneurope;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Created by GSergeev on 19.07.2017.
 */
@Component
public class Receiver {
    protected static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message) {
        LOGGER.info("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
