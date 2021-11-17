package com.example.spring.camel.playground.route.pojo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("route-pojo")
public class NumberRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    // generate random number every second
    // which is send to this seda queue that the NumberPojo will consume
    from("timer:number?period=1000")
        .transform().simple("${random(0,200)}")
        .to("direct:numbers");
  }

}