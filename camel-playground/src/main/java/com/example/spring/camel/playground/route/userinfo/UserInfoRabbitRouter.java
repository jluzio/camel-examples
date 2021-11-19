package com.example.spring.camel.playground.route.userinfo;

import com.example.spring.camel.playground.jpa.UserInfo;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("userinfo-rabbit")
public class UserInfoRabbitRouter extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("rabbitmq:userinfo?routingKey=process-user-info")
        .unmarshal().json(UserInfo.class)
        .to("seda:processUserInfo");
  }

}
