package com.example.oceansns;

import com.example.oceansns.model.MYIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Slf4j
@SpringBootApplication
public class OceanSnsApplication implements CommandLineRunner {
    @Autowired
    private MYIP myip;

    public static void main(String[] args) {
        SpringApplication.run(OceanSnsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("command line runner from main application.");
        log.info("ip main application: {}",myip.getIP());
    }
}
