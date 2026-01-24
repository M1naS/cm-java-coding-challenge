package com.crewmeister.cmcodingchallenge.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpResponse<T> {
    private final T body;
    private final int statusCode;
}