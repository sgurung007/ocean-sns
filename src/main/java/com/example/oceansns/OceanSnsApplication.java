package com.example.oceansns;

import com.example.oceansns.pureJava.HostAddressFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class OceanSnsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OceanSnsApplication.class, args);
    }
    static {
        HostAddressFinder.getHostAddress();
    }

}
