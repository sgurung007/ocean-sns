package com.example.oceansns.config;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;


@Component
public class MyPreDestroyRunner {

    @PreDestroy
    public void destroy(){
        System.out.println("from MyPreDestryRunner: hero199");
    }
}
