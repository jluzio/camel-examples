package com.example.spring.camel.playground.route;

import static org.apache.camel.model.rest.RestParamType.body;
import static org.apache.camel.model.rest.RestParamType.path;

import com.example.api.jsonplaceholder.api.v1.model.User;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class UsersRestRouter extends RouteBuilder {

  @Autowired
  private Environment env;

  @Value("${camel.servlet.mapping.context-path}")
  private String contextPath;

  @Override
  public void configure() throws Exception {

    // @formatter:off

    // this can also be configured in application.properties
    restConfiguration()
        .component("servlet")
        .bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true")
        .enableCORS(true)
        .port(env.getProperty("server.port", "8080"))
        .contextPath(contextPath.substring(0, contextPath.length() - 2))
        // turn on openapi api-doc
        .apiContextPath("/api-doc")
        .apiProperty("api.title", "User API")
        .apiProperty("api.version", "1.0.0");

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
        .route()
        .to("bean:jsonPlaceholderService?method=getUser(${header.id})", "file:/home/tmp/camel/users")

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
