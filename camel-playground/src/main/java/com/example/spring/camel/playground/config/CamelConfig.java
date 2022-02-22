package com.example.spring.camel.playground.config;

import javax.annotation.PostConstruct;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.engine.DefaultTracer;
import org.apache.camel.spi.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig {

  static class CustomTracer extends DefaultTracer {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.camel.Tracing");

    @Override
    protected void dumpTrace(String out, Object node) {
      LOG.info(out);
    }
  }

  @Autowired
  private CamelContext camelContext;

  @PostConstruct
  void init() {
    camelContext.setTracer(new CustomTracer());
  }

}
