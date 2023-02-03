package com.example.oceansns.config;

import com.example.oceansns.model.MYIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;

import java.net.InetAddress;
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
        log.info("command line runner from MyApplicationRunner.");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        log.info("myIpAddress: {}", hostAddress);
        hostAddress=hostAddress.replace(".","_");
        myip.setIP(hostAddress);
        myip.setUpdated(true);
        CompletableFuture<CreateQueueResponse> queue = sqsAsyncClient.createQueue(CreateQueueRequest.builder()
                .queueName(hostAddress)
                .build());
        CreateQueueResponse createQueueResponse = queue.get();
        log.info("created queue info: {}",createQueueResponse);
    }


}
