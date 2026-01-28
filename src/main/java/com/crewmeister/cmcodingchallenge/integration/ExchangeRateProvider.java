package com.crewmeister.cmcodingchallenge.integration;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<? extends CurrencyDto> getAllCurrencies(CurrencyRequest currencyRequest);

    List<String> getAvailableCurrencies(ExchangeRequest exchangeRequest);

    List<? extends ExchangeDto> getExchangeRates(ExchangeRequest exchangeRequest);

    JsonNode getExchangeRatesByDate(ExchangeRequest exchangeRequest);

    JsonNode getConvertedForeignExchangeAmount(ExchangeRequest exchangeRequest);
}
