package com.example.spring.camel.playground.route.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class RestConfigurationRouter extends RouteBuilder {

  @Autowired
  private Environment env;

  @Value("${camel.servlet.mapping.context-path}")
  private String contextPath;

  @Override
  public void configure() throws Exception {
    String port = env.getProperty("server.port", "8080");
    String contextPath = this.contextPath.substring(0, this.contextPath.length() - 2);

    restConfiguration()
        .component("servlet")
        .bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true")
        .enableCORS(true)
        .port(port)
        .contextPath(contextPath)
        // turn on openapi api-doc
        .apiContextPath("/api-doc")
        .apiProperty("api.title", "User API")
        .apiProperty("api.version", "1.0.0");
  }

}
