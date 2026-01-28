package com.crewmeister.cmcodingchallenge.integration;

import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankConvertedCurrencyDto;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<? extends CurrencyDto> getAllCurrencies(CurrencyRequest currencyRequest);

    List<String> getAvailableCurrencies(ExchangeRequest exchangeRequest);

    List<? extends ExchangeDto> getExchangeRates(ExchangeRequest exchangeRequest);

    ExchangeDto getExchangeRatesByDate(ExchangeRequest exchangeRequest);

    BundesbankConvertedCurrencyDto getConvertedForeignExchangeAmount(ExchangeRequest exchangeRequest);
}
