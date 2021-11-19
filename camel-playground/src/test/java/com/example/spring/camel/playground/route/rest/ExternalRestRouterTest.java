package com.example.spring.camel.playground.route.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.api.jsonplaceholder.api.v1.model.User;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CamelSpringBootTest
@SpringBootTest(
    classes = ExternalRestRouterTest.RouteConfiguration.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class ExternalRestRouterTest {

  @TestConfiguration
  static class RouteConfiguration {

    @Bean
    RouteBuilder routeBuilder() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          restConfiguration()
              .scheme("https")
              .host("jsonplaceholder.typicode.com");

          from("direct:start")
              .id("external-consumer")
              .setHeader("id", body())
              .tracing()
              .to("rest:get:users/{id}")
              .unmarshal().json(User.class)
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
  void test_external() throws Exception {
    mockOutput.reset();
    mockOutput.expectedMessageCount(1);

    producerTemplate.sendBody("direct:start", "1");

    mockOutput.assertIsSatisfied();

    var user = mockOutput.getExchanges().get(0).getIn().getBody(User.class);
    log.info("user: {}", user);
    assertThat(user)
        .isNotNull()
        .usingRecursiveComparison()
        .ignoringExpectedNullFields()
        .isEqualTo(new User().id(1));
  }

}
