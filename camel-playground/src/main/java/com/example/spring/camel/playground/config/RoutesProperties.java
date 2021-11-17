package com.example.spring.camel.playground.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.router")
@Data
public class RoutesProperties {

  @Data
  public static class FileProperties {

    private String input;
    private String output;
  }

  private FileProperties file;

}
