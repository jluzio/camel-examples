package com.example.spring.camel.playground.api;

import com.example.api.jsonplaceholder.api.v1.model.User;
import com.example.spring.camel.playground.jpa.UserInfo;
import com.example.spring.camel.playground.jpa.UserInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersApi {

  private final ProducerTemplate producerTemplate;
  private final UserInfoRepository userInfoRepository;

  @GetMapping("/default-api/users")
  @ResponseBody
  @SuppressWarnings("unchecked")
  public List<User> getUsers() {
    producerTemplate.start();
    List<User> users = producerTemplate
        .requestBody("direct:fetchUsers", null, List.class);
    producerTemplate.stop();

    return users;
  }

  @GetMapping("/default-api/data/user-infos")
  @ResponseBody
  public Iterable<UserInfo> getDataUserInfos() {
    return userInfoRepository.findAll();
  }

  @PostMapping("/default-api/data/user-infos")
  public void postDataUserInfos(@RequestBody UserInfo userInfo) {
    userInfoRepository.save(userInfo);
  }

}
