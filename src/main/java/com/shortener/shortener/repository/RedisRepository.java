package com.shortener.shortener.repository;

import com.shortener.shortener.entity.RedisUrl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<RedisUrl, String> {
    RedisUrl findByHashIdentification(String hashIdentification);
}
