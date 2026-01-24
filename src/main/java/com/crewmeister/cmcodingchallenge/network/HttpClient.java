package com.crewmeister.cmcodingchallenge.network;

public interface HttpClient {
    <T> HttpResponse<T> send(HttpRequest request, Class<T> responseType);
}