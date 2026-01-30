package com.crewmeister.cmcodingchallenge.integration;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<String> getAvailableCurrencies(ExchangeRequest exchangeRequest);

    <T extends ExchangeDto> List<T> getExchangeRates(ExchangeRequest exchangeRequest);

    ExchangeDto getExchangeRatesByDate(ExchangeRequest exchangeRequest);
}
