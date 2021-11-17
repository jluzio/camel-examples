package com.example.spring.camel.playground.route.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.api.jsonplaceholder.api.v1.JsonPlaceholderApi;
import com.example.api.jsonplaceholder.api.v1.model.User;
import com.example.spring.camel.playground.route.UserTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@CamelSpringBootTest
@SpringBootTest
@Slf4j
class LogTest {

  @TestConfiguration
  static class RouteConfiguration {

    @Bean
    RouteBuilder testRouter() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          from("timer:log?period=100")
              .routeId("logTest")
              .transform().simple("${random(0,200)}", String.class)
              // by default uses routeId as log category and level=INFO
              .log(">>> logTest: ${body}")
              .to("stream:out")
              .to("log:com.example.logTest?level=DEBUG")
              .to("mock:output");
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
  void test() throws IOException, InterruptedException {
    mockOutput.expectedMessageCount(3);

    mockOutput.assertIsSatisfied();
  }

}
