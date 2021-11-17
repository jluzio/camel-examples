package com.example.spring.camel.playground.route.multicast;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
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
class MulticastTest {

  @TestConfiguration
  static class RouteConfiguration {

    @Component("messageProcessor")
    public static class MessageProcessor {

      public String process(String value) {
        return "The message is: %s".formatted(value);
      }
    }

    @Bean
    RouteBuilder testRouter() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          // @formatter:off
          from("direct:start")
              .routeId("multicastTest")
              .multicast()
                .pipeline()
                  .to("bean:messageProcessor?method=process")
                  .to("log:com.example.MulticastTest.1?level=DEBUG")
                  .to("mock:transformed")
                .end()
                .to("stream:out")
                .to("log:com.example.MulticastTest.2?level=DEBUG")
              .end()
              .to("log:com.example.MulticastTest.3?level=DEBUG")
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
  @EndpointInject("mock:transformed")
  private MockEndpoint mockTransformed;


  @Test
  void test() throws InterruptedException {
    mockOutput.expectedMessageCount(1);
    mockOutput.expectedBodiesReceived("123");
    mockTransformed.expectedBodiesReceived("The message is: 123");

    start.sendBody("123");

    mockOutput.assertIsSatisfied();
    mockTransformed.assertIsSatisfied();
  }

}
