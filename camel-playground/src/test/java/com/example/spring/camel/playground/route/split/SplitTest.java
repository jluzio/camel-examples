package com.example.spring.camel.playground.route.split;

import com.example.spring.camel.playground.route.split.SplitTest.RouteConfiguration;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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

@CamelSpringBootTest
@SpringBootTest(classes = RouteConfiguration.class)
@Import(CamelAutoConfiguration.class)
@Slf4j
class SplitTest {

  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  static class Request {

    private List<Item> items;
  }

  @Data
  @RequiredArgsConstructor
  @AllArgsConstructor
  @Builder
  static class Item {

    private String id;
  }

  @Configuration
  static class RouteConfiguration {

    @Bean
    RouteBuilder requestRouter() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
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
              .to("mock:result")
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
          //@formatter:off
          from("direct:start-item")
              .routeId("com.example.item")
              .to("log:com.example.default?level=INFO")
              .log("${body.id}")
              .transform(simple("${body.id}"))
          ;
          //@formatter:on
        }
      };
    }
  }

  @Autowired
  private CamelContext context;
  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:result")
  private MockEndpoint mockResult;


  @Test
  void test_split() throws InterruptedException {
    mockResult.expectedBodyReceived()
        .constant(List.of("id1", "id2"));

    Request request = Request.builder()
        .items(List.of(
            Item.builder().id("id1").build(),
            Item.builder().id("id2").build()
        ))
        .build();

    start.sendBody(request);

    mockResult.assertIsSatisfied();
  }

}
