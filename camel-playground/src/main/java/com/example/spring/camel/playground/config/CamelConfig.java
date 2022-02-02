package com.example.spring.camel.playground.config;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig {

  @Autowired
  private CamelContext camelContext;

//  @Bean
//  ProducerTemplate producerTemplate() {
//    return camelContext.createProducerTemplate();
//  }
//
//  @Bean
//  ConsumerTemplate consumerTemplate() {
//    return camelContext.createConsumerTemplate();
//  }

}
