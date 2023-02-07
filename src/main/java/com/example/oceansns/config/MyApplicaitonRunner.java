package com.example.oceansns.config;

import com.example.oceansns.model.MYIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class MyApplicaitonRunner implements ApplicationRunner {
    @Autowired
    private MYIP myip;

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CompletableFuture<CreateQueueResponse> queue = sqsAsyncClient.createQueue(CreateQueueRequest.builder()
                .queueName(myip.getIP())
                .build());
//        CreateQueueResponse createQueueResponse = queue.get();
//        log.debug("created queue info: {}", createQueueResponse);
    }


}
