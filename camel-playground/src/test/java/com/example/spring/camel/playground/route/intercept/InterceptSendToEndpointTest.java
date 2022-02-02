package com.example.spring.camel.playground.route.intercept;

import java.io.IOException;
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

@CamelSpringBootTest
@SpringBootTest
@Slf4j
class InterceptSendToEndpointTest {

  @TestConfiguration
  static class RouteConfiguration {

    @Bean
    RouteBuilder testRouter() {
      return new BaseRouteBuilder() {
        @Override
        public void configure() throws Exception {
          interceptSendToEndpoint("mock:middle")
              .to("log:intercepted");
          interceptSendToEndpoint("mock:output")
              .skipSendToOriginalEndpoint()
              .to("mock:intercept");
          super.configure();
        }
      };
    }

    class BaseRouteBuilder extends RouteBuilder {
      @Override
      public void configure() throws Exception {
        // @formatter:off
        from("direct:start")
            .routeId("extensibleRoute")
            .to("mock:middle")
            .to("mock:output")
        ;
        // @formatter:on
      }
    };

  }

  @Autowired
  private CamelContext context;
  @Autowired
  private RouteBuilder testRouter;
  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:middle")
  private MockEndpoint mockMiddle;
  @EndpointInject("mock:output")
  private MockEndpoint mockOutput;
  @EndpointInject("mock:intercepted")
  private MockEndpoint mockIntercepted;


  @Test
  void test() throws IOException, InterruptedException {

    mockMiddle.expectedMessageCount(1);
    mockOutput.expectedMessageCount(0);
    mockIntercepted.expectedMessageCount(1);

    start.requestBody("test1");

    mockOutput.assertIsSatisfied();
  }

}
