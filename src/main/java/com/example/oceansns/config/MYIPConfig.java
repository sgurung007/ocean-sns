package com.example.oceansns.config;

import com.example.oceansns.model.MYIP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MYIPConfig {
    @Value("${surajQueue:demo}")
    private String SURAJ_QUEUENAME;
    @Bean
    public MYIP myip(){
        return MYIP.builder().IP(SURAJ_QUEUENAME).updated(false).build();
    }
}
