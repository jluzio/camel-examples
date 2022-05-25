package com.example.spring.camel.playground.route;


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

@CamelSpringBootTest
@SpringBootTest
// NOTE: must import CamelAutoConfiguration for regular Camel boot
@Import(CamelAutoConfiguration.class)
@Slf4j
class StandaloneRouteTest {

  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:output")
  private MockEndpoint mockOutput;

  // NOTE: doesn't load other routes
  @Configuration
  static class Config {

    @Bean
    RouteBuilder routerBuilder() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          //@formatter:off
          from("direct:start")
              .transform(simple("Hello ${body}!"))
              .to("mock:output");
          //@formatter:on
        }
      };
    }
  }

  @Test
  void test() throws Exception {
    mockOutput.expectedMessageCount(1);
    mockOutput.expectedBodyReceived()
            .constant("Hello Camel!");

    start.sendBody("Camel");

    mockOutput.assertIsSatisfied();
  }
}
