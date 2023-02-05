package com.example.oceansns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.UnknownHostException;

import static java.net.InetAddress.getLocalHost;

@Slf4j
@SpringBootApplication
public class OceanSnsApplication {

    static {
        String hostAddress = null;
        try {
            hostAddress = getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("error in static block, error message is: {}",e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("myIpAddress: {}", hostAddress);
        hostAddress = hostAddress.replace(".", "_");
        System.setProperty("surajQueue", hostAddress);
    }

    public static void main(String[] args) {
        SpringApplication.run(OceanSnsApplication.class, args);
    }

}
