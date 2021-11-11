package com.example.spring.camel.playground.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.api.jsonplaceholder.api.v1.JsonPlaceholderApi;
import com.example.api.jsonplaceholder.api.v1.model.User;
import com.example.spring.camel.playground.CamelPlaygroundApplication;
import com.example.spring.camel.playground.route.UsersDirectRouterTest.Configuration;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@CamelSpringBootTest
@SpringBootTest(classes = Configuration.class)
@Slf4j
class UsersDirectRouterTest {

  @Autowired
  private CamelContext context;
  @Autowired
  private ProducerTemplate producerTemplate;
  @Produce("direct:start")
  private ProducerTemplate start;
  @EndpointInject("mock:output")
  private MockEndpoint mockOutput;
  @MockBean
  private JsonPlaceholderApi jsonPlaceholderApi;

  private final List<User> users = UsersTestData.USERS;

  @TestConfiguration
  static class Configuration {

    @Bean
    RouteBuilder testRoute() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          from("direct:start")
              .to("direct:getUser")
              .to("mock:output");
        }
      };
    }
  }

  @Test
  void test_simple() {
    when(jsonPlaceholderApi.getUser(any()))
        .thenReturn(getExpectedUser(1));

    User user = (User) producerTemplate.sendBody("direct:getUser", ExchangePattern.InOut, "1");
    assertThat(user)
        .isNotNull()
        .usingRecursiveComparison()
        .ignoringExpectedNullFields()
        .isEqualTo(getExpectedUserSubset(1));
  }

  @Test
  void test_mock() throws InterruptedException {
    User expectedUser = getExpectedUser(1);
    User expectedUserSubset = getExpectedUserSubset(1);

    when(jsonPlaceholderApi.getUser(any()))
        .thenReturn(expectedUser);

    mockOutput.expectedBodiesReceived(expectedUser);

    mockOutput.whenAnyExchangeReceived(exchange -> {
      Message message = exchange.getIn();
      Object body = message.getBody();
      log.debug("message: {} | body: {}", message, body);
    });

    mockOutput.expectedMessagesMatches(exchange -> {
      Message message = exchange.getIn();
      User body = message.getBody(User.class);
      log.debug("message: {} | body: {}", message, body);
      assertThat(body)
          .isNotNull()
          .usingRecursiveComparison()
          .ignoringExpectedNullFields()
          .isEqualTo(expectedUserSubset);
      return true;
    });

    start.sendBody("1");

    mockOutput.assertIsSatisfied();
  }

  private User getExpectedUserSubset(int id) {
    var expectedUser = getExpectedUser(id);
    return new User()
        .id(expectedUser.getId())
        .username(expectedUser.getUsername());
  }

  private User getExpectedUser(int id) {
    return users.stream()
        .filter(u -> Objects.equals(u.getId(), id))
        .findFirst()
        .get();
  }

}
