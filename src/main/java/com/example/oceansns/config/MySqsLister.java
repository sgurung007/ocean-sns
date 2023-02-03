package com.example.oceansns.config;

import com.example.oceansns.model.MYIP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class MySqsLister {
    @Autowired
    private MYIP myip;


}
