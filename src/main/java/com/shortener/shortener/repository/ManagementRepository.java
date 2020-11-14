package com.shortener.shortener.repository;

import com.shortener.shortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementRepository extends JpaRepository<Url, Long> {
    Url findUrlByLongUrl(String long_url);
}
