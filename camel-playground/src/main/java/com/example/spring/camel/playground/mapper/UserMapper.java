package com.example.spring.camel.playground.mapper;

import com.example.api.jsonplaceholder.api.v1.model.User;
import com.example.spring.camel.playground.jpa.UserInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserInfo toUserInfo(User user);

}
