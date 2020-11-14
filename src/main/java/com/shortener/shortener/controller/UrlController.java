package com.shortener.shortener.controller;

import com.shortener.shortener.dto.UrlDTO;
import com.shortener.shortener.dto.UrlLongRequestDTO;
import com.shortener.shortener.repository.ManagementRepository;
import com.shortener.shortener.service.ManagementService;
import com.shortener.shortener.service.RedirectService;
import com.shortener.shortener.utilities.UrlConstants;
import com.shortener.shortener.utilities.UrlException;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UrlController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ManagementRepository managementRepository;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RedirectService redirectService;

    public UrlController() {
    }

    @ApiOperation(value = "Convert new url", notes = "Converts long url to short url")
    @PostMapping("create-short")
    public ResponseEntity<Object> convertToShortUrl(@RequestBody UrlLongRequestDTO request) {
        if(request == null){
            throw new UrlException(UrlConstants.NULL_EMPTY_REQUEST_URL_SHORT_URL + request);
        }
        var existingUrlObj = managementRepository.findUrlByLongUrl(request.getLongUrl());
        if (existingUrlObj != null)
            return ResponseEntity.status(HttpStatus.FOUND).body("URL already exists");
        var response = managementService.convertToShortUrl(request);
        System.out.println("Successfully created short url !");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiOperation(value = "Delete existing url")
    @DeleteMapping("delete-url/{id}")
    public ResponseEntity<Object> deleteUrl(@PathVariable String id) {
        System.out.println("Started deleting url..");
        var existingUrlObj = managementRepository.findById(Long.valueOf(id)).get();
        if (existingUrlObj== null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can not find the URL with id: " + id);
        }
        UrlDTO urlDTO = new UrlDTO();
        BeanUtils.copyProperties(existingUrlObj, urlDTO);
        managementService.deleteUrl(Long.valueOf(id), urlDTO);
        System.out.println("Successfully deleted url !");
        return ResponseEntity.status(HttpStatus.OK).body((Object) Map.of("Message", "Sucessfully deleted url"));
    }

    @ApiOperation(value = "Redirect", notes = "Finds original url from short url")
    @GetMapping(value = "{shortUrl}")
    @Cacheable(value = "urls", key = "#shortUrl", sync = true)
    public ResponseEntity<Object> getAndRedirect(@PathVariable String shortUrl)
    {
        System.out.println("Started checking if url exists..");
        String url = null;
            try { url = redirectService.getLongUrl(shortUrl); } catch(Exception e) {}
        if (url == UrlConstants.LIMIT_EXCEEDED)
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(UrlConstants.LIMIT_EXCEEDED);
        if (url != null)
            return ResponseEntity.status(HttpStatus.FOUND).body("URL already exists");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
                // to directly redirect to longUrl..
                //.location(URI.create(url))
                //.build();
    }
}
