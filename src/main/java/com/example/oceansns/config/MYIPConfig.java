package com.example.oceansns.config;

import com.example.oceansns.model.MYIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class MYIPConfig {
    @Value("${surajQueue:demo}")
    private String SURAJ_QUEUENAME;
    @Bean
    public MYIP myip(){
        return MYIP.builder().IP(SURAJ_QUEUENAME).updated(false).build();
    }

}
