package com.example.oceansns.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Slf4j
@Component
public class MyPreDestroyRunner {

    @PreDestroy
    public void destroy(){
        log.info("from MyPreDestryRunner: {}", "hero199");
    }
}
