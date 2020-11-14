package com.shortener.shortener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.shortener.shortener.dto.UrlDTO;
import com.shortener.shortener.dto.UrlLongRequestDTO;
import com.shortener.shortener.entity.Url;
import com.shortener.shortener.repository.ManagementRepository;
import com.shortener.shortener.utilities.UrlConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.shortener.shortener.configuration.RabbitMqConfig.*;

@Service
public class ManagementService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ManagementRepository managementRepository;

    public ManagementService() {
    }

    public UrlDTO convertToShortUrl(UrlLongRequestDTO request) {
        LOGGER.error("Started creating shortURL: {}", UrlConstants.NULL_EMPTY_REQUEST_URL_SHORT_URL + request);
        Url url = createShortUrl(request);
        // save to MySql..
        var entity = managementRepository.save(url);
        UrlDTO urlDTO = new UrlDTO();
        BeanUtils.copyProperties(entity, urlDTO);
        SendRabbitMQMessage(urlDTO);
        return urlDTO;
    }

    public void deleteUrl(Long id, UrlDTO existingUrlObj) {
        managementRepository.deleteById(id);
        SendRabbitMQDeleteMessage(existingUrlObj);
    }

    public Url createShortUrl(UrlLongRequestDTO request) {
        var url = new Url();
        url.setLongUrl(request.getLongUrl());
        url.setCreatedDate(new Date());
        String shortHash = Hashing.murmur3_32().hashString(request.getLongUrl(), StandardCharsets.UTF_8).toString();
        url.setHashIdentification(shortHash);
        return url;
    }

    private void SendRabbitMQDeleteMessage(UrlDTO urlDTO) {
        LOGGER.info("Sending message to RabbitMQ");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(urlDTO);
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_DELETE, json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to send message to RabbitMQ ", e.getMessage());
        }
    }

    private void SendRabbitMQMessage(UrlDTO urlDTO) {
        LOGGER.info("Sending message to RabbitMQ");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(urlDTO);
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_CREATE, json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to send message to RabbitMQ ", e.getMessage());
        }
    }
}
