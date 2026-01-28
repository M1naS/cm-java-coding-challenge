package com.crewmeister.cmcodingchallenge.integration;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.InputStream;
import java.util.List;

public interface IntegrationMapper {
    List<String> parseToAvailableCurrencies(InputStream csvInputStream);
    JsonNode parseToExchangeRate(InputStream csvInputStream);
}
