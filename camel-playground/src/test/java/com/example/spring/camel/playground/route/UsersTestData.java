package com.example.spring.camel.playground.route;

import com.example.api.jsonplaceholder.api.v1.model.User;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UsersTestData {

  public static List<User> USERS = List.of(
      new User().id(1).name("John").username("johndoe").email("johndoe@mail.org"),
      new User().id(2).name("Jane").username("janedoe").email("janedoe@mail.org"),
      new User().id(3).name("Jeff").username("jeffdoe").email("jeffdoe@mail.org")
  );

}
