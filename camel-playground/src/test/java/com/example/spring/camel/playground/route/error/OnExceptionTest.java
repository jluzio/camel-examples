package com.example.spring.camel.playground.route.error;


import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@CamelSpringBootTest
@SpringBootTest(classes = OnExceptionTest.Config.class)
@Slf4j
class OnExceptionTest {

  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:successOutput")
  private MockEndpoint mockSuccessOutput;
  @EndpointInject("mock:exceptionOutput")
  private MockEndpoint mockExceptionOutput;

  @TestConfiguration
  static class Config {

    @Bean
    RouteBuilder routerBuilder() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          onException(Exception.class)
              .handled(true)
              .setBody(exceptionMessage())
              .to("mock:exceptionOutput");

          from("direct:start")
              .to("direct:normal-service")
              .to("direct:faulty-service")
              .to("mock:successOutput");

          from("direct:normal-service")
              .bean(NormalService.class);
          from("direct:faulty-service")
              .bean(FaultyService.class);
        }
      };
    }

    @Component
    static class NormalService {

      public String get() {
        return "42";
      }
    }
  }

  @Component
  static class FaultyService {

    public String get() {
      throw new ArithmeticException("can't handle maths");
    }
  }

  @Test
  void test() throws Exception {
    mockSuccessOutput.expectedMessageCount(0);
    mockExceptionOutput.expectedMessageCount(1);
    mockExceptionOutput.expectedBodiesReceived("can't handle maths");

    start.sendBody(null);

    mockSuccessOutput.assertIsSatisfied();
    mockExceptionOutput.assertIsSatisfied();
  }

}
