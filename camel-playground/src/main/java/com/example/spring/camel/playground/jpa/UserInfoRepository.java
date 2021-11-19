package com.example.spring.camel.playground.jpa;

import java.util.stream.Stream;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Integer> {

  Stream<UserInfo> findByProcessedFalse();

}
