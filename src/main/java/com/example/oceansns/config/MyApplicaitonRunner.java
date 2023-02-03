package com.example.oceansns.config;

import com.example.oceansns.model.MYIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@Slf4j
public class MyApplicaitonRunner implements ApplicationRunner {
    @Autowired
    private MYIP myip;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("command line runner from MyApplicationRunner.");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        log.info("myIpAddress: {}", hostAddress);
        myip.setIP(hostAddress);
        myip.setUpdated(true);
        createSqsQueue(hostAddress);
    }

    private void createSqsQueue(String hostAddress) {

    }
}
