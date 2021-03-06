package com.example.spring.camel.playground.route.file;

import com.example.spring.camel.playground.processor.ArrayListAggregationStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("route-file")
public class UserFileRouter extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    //@formatter:off
    from("file:{{app.router.file.input}}?fileName=users.json&delete=true")
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
