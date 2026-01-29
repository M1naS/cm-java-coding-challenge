package com.crewmeister.cmcodingchallenge.network;

import org.springframework.http.ResponseEntity;

import java.io.InputStream;

public interface HttpGateway {
    <T> ResponseEntity<T> send(AppRequest appRequest, Class<T> responseType);
    <T> T sendAndExtract(AppRequest appRequest, Class<T> requestType, StreamExtractor<T> streamExtractor);

    @FunctionalInterface
    interface StreamExtractor<T> {
        T extract(InputStream inputStream);
    }
}