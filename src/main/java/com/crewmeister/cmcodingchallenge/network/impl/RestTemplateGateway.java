package com.crewmeister.cmcodingchallenge.network.impl;

import com.crewmeister.cmcodingchallenge.network.HttpGateway;
import com.crewmeister.cmcodingchallenge.network.AppRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

@RequiredArgsConstructor
public class RestTemplateGateway implements HttpGateway {

    private final RestTemplate restTemplate;

    @Override
    public <T> ResponseEntity<T> send(AppRequest appRequest, Class<T> responseType) {
        ResponseEntity<T> responseEntity = restTemplate.exchange(
                appRequest.getUrl(),
                HttpMethod.valueOf(appRequest.getMethod().toString()),
                new HttpEntity<>(appRequest.getBody(), appRequest.getHeaders()),
                responseType
        );

        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.valueOf(responseEntity.getStatusCodeValue()));
    }

    @Override
    public <T> InputStream stream(AppRequest appRequest, Class<T> requestType) {
        return restTemplate.execute(appRequest.getUrl(),
                HttpMethod.valueOf(appRequest.getMethod().toString()),
                restTemplate.httpEntityCallback(
                        new HttpEntity<>(appRequest.getBody(), appRequest.getHeaders()),
                        requestType
                ),
                HttpInputMessage::getBody
        );
    }
}
