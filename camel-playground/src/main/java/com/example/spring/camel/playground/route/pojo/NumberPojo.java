package com.example.spring.camel.playground.route.pojo;

import org.apache.camel.Consume;
import org.apache.camel.Produce;
import org.springframework.stereotype.Component;

@Component
public class NumberPojo {

  // sends the message to the stream:out endpoint but hidden behind this interface
  // so the client java code below can use the interface method instead of Camel's
  // FluentProducerTemplate or ProducerTemplate APIs
  @Produce("stream:out")
  private NumberProducer numberProducer;

  // only consume when the predicate matches, eg when the message body is lower than 100
  @Consume(value = "direct:numbers", predicate = "${body} < 100")
  public void lowNumber(int number) {
    numberProducer.generatedNumber("Got a low number " + number);
  }

  // only consume when the predicate matches, eg when the message body is higher or equal to 100
  @Consume(value = "direct:numbers", predicate = "${body} >= 100")
  public void highNumber(int number) {
    numberProducer.generatedNumber("Got a high number " + number);
  }

}