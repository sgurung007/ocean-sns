package com.example.oceansns.config;

import com.example.oceansns.config.annotation.SurajSqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MySqsLister {

    @SurajSqsListener
    void listen(Message<?> message) {
        log.info("body: {}", message.getPayload());
    }
}
