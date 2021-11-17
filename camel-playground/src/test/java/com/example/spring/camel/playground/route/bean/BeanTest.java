package com.example.spring.camel.playground.route.bean;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Handler;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@CamelSpringBootTest
@SpringBootTest
@Slf4j
/**
 * @see https://camel.apache.org/manual/bean-binding.html
 */
class BeanTest {

  @TestConfiguration
  static class RouteConfiguration {

    @Component("messageProcessor")
    public static class MessageProcessor {

      @Handler
      public String process(String value) {
        return value + "-process";
      }

      public String processAlt(String value) {
        return value + "-processAlt";
      }

      public String processParam(String value, boolean flag) {
        return value + "-processParam[%s]".formatted(flag);
      }
    }

    @Bean
    RouteBuilder testRouter() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          // @formatter:off
          from("direct:start")
              .routeId("beanTest")
              .bean(MessageProcessor.class)
              .bean(MessageProcessor.class, "processAlt")
              .to("bean:messageProcessor")
              .to("bean:messageProcessor?method=processAlt")
              .bean(MessageProcessor.class, "processParam(*, true)")
              .to("bean:messageProcessor?method=processParam(*, false)")
              .to("mock:output")
          ;
          // @formatter:on
        }
      };
    }
  }

  @Autowired
  private CamelContext context;
  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:output")
  private MockEndpoint mockOutput;


  @Test
  void test() throws InterruptedException {
    mockOutput.expectedMessageCount(1);
    mockOutput.expectedBodiesReceived(
        "start-process-processAlt-process-processAlt-processParam[true]-processParam[false]");

    start.sendBody("start");

    mockOutput.assertIsSatisfied();
  }

}
