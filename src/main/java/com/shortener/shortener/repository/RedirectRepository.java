package com.shortener.shortener.repository;

import com.shortener.shortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedirectRepository extends JpaRepository<Url, Long> {
    Url findUrlByHashIdentification(String hash_identification);
}

