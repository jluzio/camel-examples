package com.example.spring.camel.playground.service;

import com.example.api.jsonplaceholder.api.v1.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JsonPlaceholderServiceTest {

  @Autowired
  JsonPlaceholderService service;

  @Test
  void getUsers() {
    List<User> users = service.getUsers();
    System.out.println(users);
  }

  @Test
  void getUser() {
  }
}