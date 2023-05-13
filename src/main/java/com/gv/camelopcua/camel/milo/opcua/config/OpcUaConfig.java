package com.gv.camelopcua.camel.milo.opcua.config;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class OpcUaConfig {
    @Bean
    public SpringCamelContext camelContext() {
        return new SpringCamelContext();
    }
    @Bean
    public ConcurrentHashMap<String, ProducerTemplate> producers() {
        return new ConcurrentHashMap<>();
    }
}
