package com.crewmeister.cmcodingchallenge.integration;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.InputStream;

public interface IntegrationMapper {
    JsonNode parseToAvailableCurrencies(InputStream csvInputStream);
    JsonNode parseToExchangeRate(InputStream csvInputStream);
}
