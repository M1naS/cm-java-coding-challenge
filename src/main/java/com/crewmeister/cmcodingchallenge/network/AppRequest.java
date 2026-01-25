package com.crewmeister.cmcodingchallenge.network;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.MultiValueMap;

@Getter
@Builder
public class AppRequest {
    private final MultiValueMap<String, String> headers;
    private final HttpMethod method;
    private final String url;
    private final Object body;

    public enum HttpMethod {
        POST, GET, PUT, DELETE
    }
}