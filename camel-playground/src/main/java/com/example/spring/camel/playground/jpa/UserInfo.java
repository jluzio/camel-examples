package com.example.spring.camel.playground.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserInfo {

  @Id
  @GeneratedValue
  private int id;

  private String username;
  private boolean processed;
  private int albumCount;
  private int postCount;
  private int todoCount;

}
