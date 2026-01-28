package com.crewmeister.cmcodingchallenge.integration;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<? extends CurrencyDto> getAllCurrencies(CurrencyRequest request);

    JsonNode getAvailableCurrencies(CurrencyRequest request);

    JsonNode getExchangeRates(ExchangeRequest request);

    JsonNode getExchangeRatesByDate(ExchangeRequest request);

    JsonNode getConvertedForeignExchangeAmount(ExchangeRequest request);
}
