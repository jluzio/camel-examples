package com.example.spring.camel.playground.route.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CamelSpringBootTest
@SpringBootTest(
    classes = LocalRestRouterTest.RouteConfiguration.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class LocalRestRouterTest {

  @TestConfiguration
  static class RouteConfiguration {

    @RestController
    static class Controller {

      @GetMapping("/hello")
      public String hello() {
        return "Hello world";
      }
    }

    @Bean
    RouteBuilder routeBuilder(Environment environment) {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          restConfiguration()
              .host("localhost")
              .port(environment.getRequiredProperty("local.server.port"));

          from("direct:start")
              .id("local-consumer")
              .tracing()
              .to("rest:get:hello")
              .to("mock:output")
              .log("${body}");
        }
      };
    }
  }

  @Autowired
  private CamelContext context;
  @Autowired
  private ProducerTemplate producerTemplate;
  @EndpointInject("mock:output")
  private MockEndpoint mockOutput;

  @Test
  void test_local() throws Exception {
    mockOutput.expectedBodiesReceived("Hello world");

    producerTemplate.sendBody("direct:start", null);

    mockOutput.assertIsSatisfied();
  }

}
