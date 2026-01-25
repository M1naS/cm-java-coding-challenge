package com.crewmeister.cmcodingchallenge.network;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppResponse<T> {
    private final T body;
    private final int statusCode;
}