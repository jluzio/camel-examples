package com.example.spring.camel.playground.route.userinfo;

import static org.apache.camel.model.rest.RestParamType.path;

import com.example.api.jsonplaceholder.api.v1.model.User;
import com.example.spring.camel.playground.jpa.UserInfo;
import com.example.spring.camel.playground.service.UserInfoService;
import java.util.NoSuchElementException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@Profile({"userinfo", "userinfo-timer", "userinfo-rabbit"})
public class UserInfoRestRouter extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    onException(NoSuchElementException.class)
        .handled(true)
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_FOUND.value()))
        .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
        .setBody(exceptionMessage());

    var api = rest("/user-infos").description("User Infos REST service")
        .consumes(MediaType.APPLICATION_JSON_VALUE)
        .produces(MediaType.APPLICATION_JSON_VALUE);
    // @formatter:off
    api.get().id("rest-user-infos").description("Find all users")
        .outType(UserInfo[].class)
        .responseMessage().code(HttpStatus.OK.value()).message("All users successfully returned").endResponseMessage()
        .to("direct:getUserInfos");
    api.get("/{id}").id("rest-user-info").description("Find user by ID")
        .outType(User.class)
        .param().name("id").type(path).description("The ID of the user").dataType("integer").endParam()
        .responseMessage().code(HttpStatus.OK.value()).message("User successfully returned").endResponseMessage()
        .route()
          .transform(simple("${header.id}", Integer.class))
          .to("direct:getUserInfo");
    // @formatter:on

    from("direct:getUserInfos")
        .bean(UserInfoService.class, "findUserInfos");

    from("direct:getUserInfo")
        .bean(UserInfoService.class, "getUserInfo");

    from("seda:processUserInfo")
        .bean(UserInfoService.class, "processUser")
        .bean(UserInfoService.class, "processUserAlbums")
        .bean(UserInfoService.class, "processUserPosts")
        .bean(UserInfoService.class, "processUserTodos")
        .log("${body}")
        .bean(UserInfoService.class, "saveProcessedUserInfo");
  }

}
