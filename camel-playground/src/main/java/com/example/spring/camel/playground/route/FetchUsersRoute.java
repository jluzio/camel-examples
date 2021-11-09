package com.example.spring.camel.playground.route;

import com.example.spring.camel.playground.service.JsonPlaceholderService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FetchUsersRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("direct:fetchUsers")
        .routeId("direct-fetchUsers")
        .tracing()
        .log(">>> ${body}")
        .bean(JsonPlaceholderService.class, "getUsers")
        .end();
  }
}
