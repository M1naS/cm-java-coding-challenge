package com.crewmeister.cmcodingchallenge.integration;

import com.crewmeister.cmcodingchallenge.integration.dto.*;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<? extends CurrencyDto> getAllCurrencies(CurrencyRequest request);

    JsonNode getAvailableCurrencies(CurrencyRequest request);

    List<? extends ExchangeDto> getExchangeRates(ExchangeRequest request);
}
