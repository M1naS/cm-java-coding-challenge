package com.crewmeister.cmcodingchallenge.network.impl;

import com.crewmeister.cmcodingchallenge.network.HttpGateway;
import com.crewmeister.cmcodingchallenge.network.AppRequest;
import com.crewmeister.cmcodingchallenge.network.AppResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class RestTemplateGateway implements HttpGateway {

    private final RestTemplate restTemplate;

    @Override
    public <T> AppResponse<T> send(AppRequest request, Class<T> responseType) {
        ResponseEntity<T> responseEntity = restTemplate.exchange(
                request.getUrl(),
                HttpMethod.valueOf(request.getMethod().toString()),
                new HttpEntity<>(request.getBody(), request.getHeaders()),
                responseType
        );

        return new AppResponse<>(responseEntity.getBody(), responseEntity.getStatusCodeValue());
    }
}
