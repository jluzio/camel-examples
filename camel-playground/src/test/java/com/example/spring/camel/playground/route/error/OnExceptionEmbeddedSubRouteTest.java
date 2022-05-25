package com.example.spring.camel.playground.route.error;


import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@CamelSpringBootTest
@SpringBootTest
@Import(CamelAutoConfiguration.class)
@Slf4j
class OnExceptionEmbeddedSubRouteTest {

  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:successOutput")
  private MockEndpoint mockSuccessOutput;
  @EndpointInject("mock:exceptionOutput")
  private MockEndpoint mockExceptionOutput;

  @Configuration
  static class Config {

    @Bean
    RouteBuilder routerBuilder() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          //@formatter:off
          from("direct:start")
              .pipeline()
                .to("direct:normal-service")
                .to("direct:faulty-service")
                .to("direct:normal-service")
              .end()
              .to("mock:successOutput");

          from("direct:normal-service")
              .bean(NormalService.class);
          from("direct:faulty-service")
              .onException(Exception.class)
                .handled(true)
                .bean(RecoveryService.class)
//                .transform(exceptionMessage())
              .end()
              .bean(FaultyService.class);
          //@formatter:on
        }
      };
    }

    @Component
    static class NormalService {

      public String process(String previousValue) {
        return String.join("-", previousValue, "42");
      }
    }

    @Component
    static class FaultyService {

      public String process(String previousValue) {
        throw new ArithmeticException("can't handle maths");
      }
    }

    @Component
    static class RecoveryService {

      public String process(String previousValue) {
        return String.join("-", previousValue, "recovered");
      }
    }
  }

  @Test
  void test() throws Exception {
    mockSuccessOutput.expectedMessageCount(0);
    mockExceptionOutput.expectedMessageCount(0);

    start.sendBody("start");

    mockSuccessOutput.assertIsSatisfied();
    mockExceptionOutput.assertIsSatisfied();
  }
}
