package com.crewmeister.cmcodingchallenge.network.impl;

import com.crewmeister.cmcodingchallenge.network.HttpGateway;
import com.crewmeister.cmcodingchallenge.network.AppRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class RestTemplateGateway implements HttpGateway {

    private final RestTemplate restTemplate;

    @Override
    public <T> ResponseEntity<T> send(AppRequest request, Class<T> responseType) {
        ResponseEntity<T> responseEntity = restTemplate.exchange(
                request.getUrl(),
                HttpMethod.valueOf(request.getMethod().toString()),
                new HttpEntity<>(request.getBody(), request.getHeaders()),
                responseType
        );

        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.valueOf(responseEntity.getStatusCodeValue()));
    }
}
