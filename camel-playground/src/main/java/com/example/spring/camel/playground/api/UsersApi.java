package com.example.spring.camel.playground.api;

import com.example.api.jsonplaceholder.api.v1.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersApi {

  private final ProducerTemplate producerTemplate;

  @GetMapping("/users")
  @ResponseBody
  public List<User> getUsers() {
    producerTemplate.start();
    List<User> users = producerTemplate
        .requestBody("direct:fetchUsers", null, List.class);
    producerTemplate.stop();

    return users;
  }

}
