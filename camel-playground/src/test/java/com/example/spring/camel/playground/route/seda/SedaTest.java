package com.example.spring.camel.playground.route.seda;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@CamelSpringBootTest
@SpringBootTest
@Slf4j
class SedaTest {

  @TestConfiguration
  static class RouteConfiguration {

    @Bean
    RouteBuilder testRouter() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          from("seda:foo?multipleConsumers=true").routeId("foo").to("mock:foo");
          from("seda:foo?multipleConsumers=true").routeId("bar").to("mock:bar");

          from("seda:concurrentConsumers?concurrentConsumers=5")
              .process(exchange -> Thread.sleep(500L))
//              .delay(5000)
              .transform(body())
              .to("mock:concurrentConsumersResult");

          from("seda:threadPool")
              .threads(5)
              .process(exchange -> Thread.sleep(500L))
//              .delay(5000)
              .transform(body())
              .to("mock:threadPoolResult");
        }
      };
    }
  }

  @Autowired
  private CamelContext context;
  @Autowired
  private ProducerTemplate template;
  @EndpointInject("mock:result")
  private MockEndpoint mockResult;
  @EndpointInject("mock:foo")
  private MockEndpoint mockFoo;
  @EndpointInject("mock:bar")
  private MockEndpoint mockBar;


  @Test
  void test_multiple_consumers() throws InterruptedException {
    mockFoo.expectedBodiesReceived("Hello World");
    mockBar.expectedBodiesReceived("Hello World");

    template.sendBody("seda:foo", "Hello World");

    mockFoo.assertIsSatisfied();
    mockBar.assertIsSatisfied();
  }

  @Test
  void test_concurrent_consumers() throws InterruptedException {
    var mockResult = context.getEndpoint("mock:concurrentConsumersResult", MockEndpoint.class);
    mockResult.expectedBodiesReceivedInAnyOrder(
        IntStream.rangeClosed(1, 10)
            .boxed()
            .collect(Collectors.toList()));

    IntStream.rangeClosed(1, 10)
            .forEach(number -> template.sendBody("seda:concurrentConsumers", number));

    mockResult.assertIsSatisfied();
  }

  @Test
  void test_thread_pool() throws InterruptedException {
    var mockResult = context.getEndpoint("mock:threadPoolResult", MockEndpoint.class);
    mockResult.expectedBodiesReceivedInAnyOrder(
        IntStream.rangeClosed(1, 10)
            .boxed()
            .collect(Collectors.toList()));

    IntStream.rangeClosed(1, 10)
            .forEach(number -> template.sendBody("seda:threadPool", number));

    mockResult.assertIsSatisfied();
  }

}
