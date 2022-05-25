package com.example.spring.camel.playground.route.error;


import java.util.List;
import java.util.Objects;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.boot.CamelAutoConfiguration;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@CamelSpringBootTest
@SpringBootTest
@Import(CamelAutoConfiguration.class)
@Slf4j
class OnExceptionSubRouteTest {

  @Value
  static class Request {

    List<Item> items;
  }

  @Value
  static class Item {

    String id;
  }

  @Configuration
  static class RouteConfiguration {

    @Bean
    RouteBuilder requestRouter() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          onException(Exception.class)
              .handled(true)
              .bean(ItemProcessor.class, "processGlobalError");

          //@formatter:off
          from("direct:start")
              .routeId("com.example.request")
              .to("log:com.example.default?level=INFO")
              .log("${body.items}")
              .split(simple("${body.items}"), AggregationStrategies.groupedBody())
//              .split(simple("${body.items}"), new ArrayListAggregationStrategy())
              .streaming()
              .to("log:com.example.default?level=INFO")
              .log("${body.id}")
              .to("direct:start-item")
              .end()
              .to("mock:successOutput")
          ;
          //@formatter:on
        }
      };
    }

    @Bean
    RouteBuilder itemRouter() {

      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          onException(Exception.class)
              .handled(true)
              .bean(ItemProcessor.class, "processError");

          //@formatter:off
          from("direct:start-item")
              .routeId("com.example.item")
              .to("log:com.example.default?level=INFO")
              .log("${body.id}")
              .transform(simple("${body.id}"))
              .bean(ItemProcessor.class, "process");
          ;
          //@formatter:on
        }
      };
    }

    @Component
    static class ItemProcessor {

      public String process(String value) {
        validate(value);
        return "Received: %s".formatted(value);
      }

      public String processError(String value) {
        return "Error for: %s".formatted(value);
      }

      public String processGlobalError(String value) {
        return "Global error :: %s".formatted(value);
      }

      private void validate(String value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(Integer.valueOf(value));
      }
    }
  }

  @Autowired
  private CamelContext context;
  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:successOutput")
  private MockEndpoint mockSuccessOutput;
  @EndpointInject("mock:exceptionOutput")
  private MockEndpoint mockExceptionOutput;


  @Test
  void test_split_exception() throws InterruptedException {
    mockExceptionOutput.expectedMessageCount(0);
    mockSuccessOutput.expectedBodyReceived()
        .constant(List.of("Received: 1", "Error for: corrupted-value", "Received: 3"));

    Request request = new Request(
        List.of(
            new Item("1"),
            new Item("corrupted-value"),
            new Item("3")
        ));

    start.sendBody(request);

    mockExceptionOutput.assertIsSatisfied();
    mockSuccessOutput.assertIsSatisfied();
  }
}
