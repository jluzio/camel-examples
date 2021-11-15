package com.example.spring.camel.playground.route;

import com.example.spring.camel.playground.processor.ArrayListAggregationStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersFileRouter extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    //@formatter:off
    from("file:{{app.router.file.input}}?filename=users.json&delete=true")
        .unmarshal().json(Integer[].class)
        .split(body(), new ArrayListAggregationStrategy())
//        .streaming()
        .parallelProcessing()
          .to("direct:getUser")
        .end()
        .marshal().json()
        .to("file:{{app.router.file.output}}")
        .log("!!! Completed !!!")
    ;
    //@formatter:on
  }
}
