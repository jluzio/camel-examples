package com.example.spring.camel.playground.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.router")
@Data
public class RoutesConfig {

  @Data
  public static class FileConfig {

    private String input;
    private String output;
  }

  private FileConfig file;

}
