package com.crewmeister.cmcodingchallenge.integration;

import java.io.InputStream;
import java.util.List;

public interface IntegrationMapper {
    List<String> parseToAvailableCurrencies(InputStream csvInputStream);
    List<? extends ExchangeDto> parseToExchangeRate(InputStream csvInputStream);
}
