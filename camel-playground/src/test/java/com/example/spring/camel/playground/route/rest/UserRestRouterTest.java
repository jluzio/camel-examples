package com.example.spring.camel.playground.route.rest;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.api.jsonplaceholder.api.v1.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@CamelSpringBootTest
@SpringBootTest(
    classes = LocalRestRouterTest.RouteConfiguration.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class UserRestRouterTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void test() {
    ResponseEntity<User> user = restTemplate.getForEntity("/api/users/1", User.class);
    assertThat(user)
        .isNotNull()
        .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(new User().id(1));
  }

}
