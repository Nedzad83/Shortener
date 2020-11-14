package com.shortener.shortener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortener.shortener.configuration.RabbitMqConfig;
import com.shortener.shortener.entity.RedisUrl;
import com.shortener.shortener.repository.RedisRepository;
import com.shortener.shortener.utilities.UrlConstants;
import com.shortener.shortener.utilities.UrlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class RedirectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectService.class);

    @Autowired
    RedisRepository redisRepository;

    public RedirectService() {
    }

    public String getLongUrl(String hashcode) {
        if (hashcode == null) {
            LOGGER.error("hashcode is null or empty");
            throw new UrlException(UrlConstants.NULL_EMPTY_HASH_CODE + hashcode);
        }
        LOGGER.info("getting url for hashcode: {}", hashcode);
        RedisUrl longUrlObj = redisRepository.findByHashIdentification(hashcode);
        if (longUrlObj == null) {
            LOGGER.error("long url is null or empty");
            throw new UrlException(UrlConstants.INVALID_URL + hashcode);
        } else {
            Date lastDate = longUrlObj.getCreatedDate();
            var counter = longUrlObj.getCounter();
            if (lastDate != null) {
                long t = lastDate.getTime();
                long current = System.currentTimeMillis();
                if (t + 120000 > current && counter == 10) {
                    // exceeded the limit..
                    longUrlObj.setCreatedDate(new Date());
                    redisRepository.save(longUrlObj);
                    return UrlConstants.LIMIT_EXCEEDED;
                } else if (t + 240000 < current) // if user try to get info again 2 mins after exceeded the limit.. reset the status
                {
                    longUrlObj.setCounter(1);
                    longUrlObj.setCreatedDate(new Date());
                } else {
                    longUrlObj.setCounter(Integer.valueOf(counter) + 1);
                }
            } else {
                longUrlObj.setCreatedDate(new Date());
                longUrlObj.setCounter(Integer.valueOf(counter) + 1);
            }

            redisRepository.save(longUrlObj);
            String longUrl = longUrlObj.getLongUrl();
            LOGGER.info("long url is: {}", longUrl);
            return longUrl;
        }
    }

    @RabbitListener(queues = {RabbitMqConfig.TOPIC_QUEUE_1_NAME})
    public void receiveMessageFromTopic1(String message) throws JsonProcessingException {
        LOGGER.info("Received create message from RabbitMQ at Redirect Service");
        RedisUrl redisUrl = new ObjectMapper().readValue(message, RedisUrl.class);
        redisRepository.save(redisUrl);
        LOGGER.info("Successfully created object into Redis database");
    }

    @RabbitListener(queues = {RabbitMqConfig.TOPIC_QUEUE_2_NAME})
    public void receiveMessageFromTopic2(String message) throws JsonProcessingException {
        LOGGER.info("Received delete message from RabbitMQ at Redirect Service");
        RedisUrl redisUrl = new ObjectMapper().readValue(message, RedisUrl.class);
        var existingUrl = redisRepository.findByHashIdentification(redisUrl.getHashIdentification());
        if (existingUrl != null)
            redisRepository.delete(existingUrl);
        LOGGER.info("Successfully deleted object from Redis database");
    }
}
