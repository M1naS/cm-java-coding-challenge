package com.crewmeister.cmcodingchallenge.network.impl;

import com.crewmeister.cmcodingchallenge.network.HttpClient;
import com.crewmeister.cmcodingchallenge.network.HttpRequest;
import com.crewmeister.cmcodingchallenge.network.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class RestTemplateClient implements HttpClient {

    private final RestTemplate restTemplate;

    @Override
    public <T> HttpResponse<T> send(HttpRequest request, Class<T> responseType) {
        ResponseEntity<T> responseEntity = restTemplate.exchange(
                request.getUrl(),
                HttpMethod.valueOf(request.getMethod().toString()),
                new HttpEntity<>(request.getBody(), request.getHeaders()),
                responseType
        );

        return new HttpResponse<>(responseEntity.getBody(), responseEntity.getStatusCodeValue());
    }
}
