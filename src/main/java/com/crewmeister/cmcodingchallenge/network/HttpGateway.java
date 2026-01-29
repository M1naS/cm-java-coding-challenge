package com.crewmeister.cmcodingchallenge.network;

import org.springframework.http.ResponseEntity;

import java.io.InputStream;

public interface HttpGateway {
    <T> ResponseEntity<T> send(AppRequest appRequest, Class<T> responseType);
    <T> InputStream stream(AppRequest appRequest, Class<T> requestType);
}