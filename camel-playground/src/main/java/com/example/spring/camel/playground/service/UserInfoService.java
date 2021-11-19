package com.example.spring.camel.playground.service;

import com.example.spring.camel.playground.jpa.UserInfo;
import com.example.spring.camel.playground.jpa.UserInfoRepository;
import com.example.spring.camel.playground.mapper.UserMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserInfoService {

  private final UserInfoRepository userInfoRepository;
  private final UserMapper userMapper;
  private final JsonPlaceholderService jsonPlaceholderService;


  public Iterable<UserInfo> findUserInfos() {
    return userInfoRepository.findAll();
  }

  public UserInfo getUserInfo(Integer id) {
    return userInfoRepository.findById(id).get();
  }

  public List<UserInfo> findUnprocessedUserInfos() {
    return userInfoRepository.findByProcessedFalse().collect(Collectors.toList());
  }

  public UserInfo processUser(UserInfo userInfo) {
    if (userInfo.getUsername() == null) {
      var user = jsonPlaceholderService.getUser(userInfo.getId());
      userInfo = userMapper.toUserInfo(user);
    }
    return userInfo;
  }

  public UserInfo processUserAlbums(UserInfo userInfo) {
    var albums = jsonPlaceholderService.getUserAlbums(userInfo.getId());
    userInfo.setAlbumCount(albums.size());
    return userInfo;
  }

  public UserInfo processUserPosts(UserInfo userInfo) {
    var posts = jsonPlaceholderService.getUserPosts(userInfo.getId());
    userInfo.setPostCount(posts.size());
    return userInfo;
  }

  public UserInfo processUserTodos(UserInfo userInfo) {
    var todos = jsonPlaceholderService.getUserTodos(userInfo.getId());
    userInfo.setTodoCount(todos.size());
    return userInfo;
  }

  public void saveProcessedUserInfo(UserInfo userInfo) {
    userInfo.setProcessed(true);
    userInfoRepository.save(userInfo);
  }

}
