package com.crewmeister.cmcodingchallenge.network;

public interface HttpGateway {
    <T> AppResponse<T> send(AppRequest request, Class<T> responseType);
}