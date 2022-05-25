package com.example.spring.camel.playground.route.rest;

import static org.apache.camel.model.rest.RestParamType.path;

import com.example.api.jsonplaceholder.api.v1.model.User;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class UserRestRouter extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    // @formatter:off

    rest("/users").description("User REST service")
        .consumes(MediaType.APPLICATION_JSON_VALUE)
        .produces(MediaType.APPLICATION_JSON_VALUE)

        .get().id("getUsers").description("Find all users")
        .outType(User[].class)
        .responseMessage().code(HttpStatus.OK.value()).message("All users successfully returned").endResponseMessage()
        .to("bean:jsonPlaceholderService?method=getUsers")

        .get("/{id}").id("getUser").description("Find user by ID")
        .outType(User.class)
        .param().name("id").type(path).description("The ID of the user").dataType("integer").endParam()
        .responseMessage().code(HttpStatus.OK.value()).message("User successfully returned").endResponseMessage()
        .to("bean:jsonPlaceholderService?method=getUser(${header.id})")

//        .put("/{id}").description("Update a user").type(User.class)
//        .param().name("id").type(path).description("The ID of the user to update").dataType("integer").endParam()
//        .param().name("body").type(body).description("The user to update").endParam()
//        .responseMessage().code(204).message("User successfully updated").endResponseMessage()
//        .to("direct:update-user")
    ;

//    from("direct:update-user")
//        .to("bean:userService?method=updateUser")
//        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
//        .setBody(constant(""));

    // @formatter:on
  }

}
