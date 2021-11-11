package com.example.spring.camel.playground.processor;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class ArrayListAggregationStrategy implements AggregationStrategy {

  @Override
  @SuppressWarnings("unchecked")
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    Object newBody = newExchange.getIn().getBody();
    List<Object> list = null;
    if (oldExchange == null) {
      list = new ArrayList<>();
      list.add(newBody);
      newExchange.getIn().setBody(list);
      return newExchange;
    } else {
      list = oldExchange.getIn().getBody(ArrayList.class);
      list.add(newBody);
      return oldExchange;
    }
  }
}
