package com.crewmeister.cmcodingchallenge.network;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class HttpRequest {
    private final Map<String, String> headers;
    private final HttpMethod method;
    private final String url;
    private final Object body;

    public enum HttpMethod {
        POST, GET, PUT, DELETE
    }
}