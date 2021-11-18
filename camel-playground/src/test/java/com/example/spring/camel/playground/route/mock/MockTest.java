package com.example.spring.camel.playground.route.mock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.vavr.control.Try;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@CamelSpringBootTest
@SpringBootTest
@Slf4j
class MockTest {

  @TestConfiguration
  static class RouteConfiguration {

    @Bean
    RouteBuilder testRouter() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          from("direct:start").routeId("start")
              .to("direct:foo").to("log:foo").to("mock:result");

          from("direct:foo").routeId("foo")
              .transform(constant("Bye World"));
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

  @BeforeEach
  void setup() {
    // need to reset due to calling the route several times
    mockResult.reset();
  }

  @Test
  void testSimple() throws InterruptedException {
    mockResult.expectedBodiesReceived("Bye World");

    template.sendBody("direct:start", "Hello World");

    mockResult.assertIsSatisfied();
  }


  @Test
  void testAdvisedMockEndpoints() throws Exception {
    // advice the start route using the inlined AdviceWith lambda style route builder
    // which has extended capabilities than the regular route builder
    AdviceWith.adviceWith(
        context,
        "start",
        // mock all endpoints
        a -> a.mockEndpoints());

    MockEndpoint mockDirectStart = (MockEndpoint) context.getEndpoint(("mock:direct:start"));
    MockEndpoint mockDirectFoo = (MockEndpoint) context.getEndpoint(("mock:direct:foo"));
    MockEndpoint mockLogFoo = (MockEndpoint) context.getEndpoint(("mock:log:foo"));
    MockEndpoint mockResult = (MockEndpoint) context.getEndpoint(("mock:result"));
    List<MockEndpoint> mockEndpoints = List.of(
        mockDirectStart, mockDirectFoo, mockLogFoo, mockResult);

    mockDirectStart.expectedBodiesReceived("Hello World");
    mockDirectFoo.expectedBodiesReceived("Hello World");
    mockLogFoo.expectedBodiesReceived("Bye World");
    mockResult.expectedBodiesReceived("Bye World");

    template.sendBody("direct:start", "Hello World");

    mockEndpoints.forEach(mockEndpoint -> Try.run(mockEndpoint::assertIsSatisfied));

    // additional test to ensure correct endpoints in registry
    assertNotNull(context.hasEndpoint("direct:start"));
    assertNotNull(context.hasEndpoint("direct:foo"));
    assertNotNull(context.hasEndpoint("log:foo"));
    assertNotNull(context.hasEndpoint("mock:result"));
    // all the endpoints was mocked
    assertNotNull(context.hasEndpoint("mock:direct:start"));
    assertNotNull(context.hasEndpoint("mock:direct:foo"));
    assertNotNull(context.hasEndpoint("mock:log:foo"));
  }

}
