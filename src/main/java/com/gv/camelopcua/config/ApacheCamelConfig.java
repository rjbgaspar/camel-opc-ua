package com.gv.camelopcua.config;

import org.apache.camel.ProducerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ApacheCamelConfig {

    @Bean
    public ConcurrentHashMap<String, ProducerTemplate> producers() {
        return new ConcurrentHashMap<>();
    }
}
