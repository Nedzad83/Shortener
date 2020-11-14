package com.shortener.shortener.entity;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.stereotype.Component;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@RedisHash("url")
public class RedisUrl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String longUrl;

    @Indexed
    private String hashIdentification;
    private Date createdDate;
    private int counter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongUrl() {
        return longUrl;
    }
    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getHashIdentification() {
        return hashIdentification;
    }
    public void setHashIdentification(String hashIdentification) {
        this.hashIdentification = hashIdentification;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getCounter() {
        return counter;
    }
    public void setCounter(int counter) {
        this.counter = counter;
    }
}
