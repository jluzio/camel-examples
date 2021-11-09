package com.example.spring.camel.playground.service;

import com.example.api.jsonplaceholder.api.v1.JsonPlaceholderApi;
import com.example.api.jsonplaceholder.api.v1.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JsonPlaceholderService {

  private final JsonPlaceholderApi api;


  public List<User> getUsers() {
    return api.getUsers();
  }

  public User getUser(Integer id) {
    return api.getUser(id);
  }

}
