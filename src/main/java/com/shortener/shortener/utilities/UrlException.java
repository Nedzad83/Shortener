package com.shortener.shortener.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlException  extends RuntimeException{
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlException.class);
    public UrlException(String message){
        super(message);
    }
}