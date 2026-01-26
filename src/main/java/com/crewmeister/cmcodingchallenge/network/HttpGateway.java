package com.crewmeister.cmcodingchallenge.network;

import org.springframework.http.ResponseEntity;

public interface HttpGateway {
    <T> ResponseEntity<T> send(AppRequest request, Class<T> responseType);
}