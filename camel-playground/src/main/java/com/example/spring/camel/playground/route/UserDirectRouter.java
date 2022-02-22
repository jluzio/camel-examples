package com.example.spring.camel.playground.route;

import com.example.spring.camel.playground.service.JsonPlaceholderService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserDirectRouter extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("direct:getUsers")
        .routeId("direct-getUsers")
        .log(">>> ${body}")
        .bean(JsonPlaceholderService.class, "getUsers")
        .end();

    from("direct:getUser")
        .routeId("direct-getUser")
        .log(">>> ${body}")
        .bean(JsonPlaceholderService.class, "getUser")
        .end();
  }
}
