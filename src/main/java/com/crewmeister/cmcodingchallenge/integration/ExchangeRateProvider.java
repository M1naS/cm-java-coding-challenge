package com.crewmeister.cmcodingchallenge.integration;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<String> getAvailableCurrencies(ExchangeRequest exchangeRequest);

    List<? extends ExchangeDto> getExchangeRates(ExchangeRequest exchangeRequest);

    List<? extends ExchangeDto> getCachedExchangeRates(ExchangeRequest exchangeRequest);

    ExchangeDto getExchangeRatesByDate(ExchangeRequest exchangeRequest);

    ConvertedCurrencyDto getConvertedForeignExchangeAmount(ExchangeRequest exchangeRequest);
}
