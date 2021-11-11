package com.example.spring.camel.playground.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.api.jsonplaceholder.api.v1.JsonPlaceholderApi;
import com.example.api.jsonplaceholder.api.v1.model.User;
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
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@CamelSpringBootTest
@SpringBootTest(
    properties = {
        "app.router.file.input=target/data/input",
        "app.router.file.output=target/data/output"
    })
@Slf4j
class UsersFileRouterTest {

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
  @Autowired
  private ObjectMapper objectMapper;

  private final List<User> users = UsersTestData.USERS;

  @Test
  void test_simple() throws IOException, InterruptedException {
    File input = new File("target/data/input/users.json");
    File output = new File("target/data/output");

    when(jsonPlaceholderApi.getUser(1))
        .thenReturn(getExpectedUser(1));
    when(jsonPlaceholderApi.getUser(2))
        .thenReturn(getExpectedUser(2));
    when(jsonPlaceholderApi.getUser(3))
        .thenReturn(getExpectedUser(3));

    input.delete();
    input.getParentFile().mkdirs();

    NotifyBuilder notification = new NotifyBuilder(context)
        .wereSentTo("file:target/data/output").whenDone(1)
        .create();

    objectMapper.writeValue(input, List.of(1, 2, 3));

    assertThat(notification.matches(2, TimeUnit.SECONDS))
        .isTrue();
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
