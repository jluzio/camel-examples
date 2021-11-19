package com.example.spring.camel.playground.service;

import com.example.api.jsonplaceholder.api.v1.JsonPlaceholderApi;
import com.example.api.jsonplaceholder.api.v1.model.Album;
import com.example.api.jsonplaceholder.api.v1.model.Post;
import com.example.api.jsonplaceholder.api.v1.model.Todo;
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
    return api.getUsers(null, null, null, null);
  }

  public User getUser(Integer id) {
    return api.getUser(id);
  }

  public List<Album> getUserAlbums(Integer id) {
    return api.getUserAlbums(id);
  }

  public List<Post> getUserPosts(Integer id) {
    return api.getUserPosts(id);
  }

  public List<Todo> getUserTodos(Integer id) {
    return api.getUserTodos(id);
  }

}
