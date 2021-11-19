package com.example.spring.camel.playground.route.userinfo;

import com.example.spring.camel.playground.service.UserInfoService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("userinfo-timer")
public class UserInfoTimerRouter extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("timer:user-info?delay=10000&period=10000")
        .bean(UserInfoService.class, "findUnprocessedUserInfos")
        .split(body()).parallelProcessing()
        .to("seda:processUserInfo");
  }

}
