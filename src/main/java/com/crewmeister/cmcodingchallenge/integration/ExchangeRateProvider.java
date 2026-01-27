package com.crewmeister.cmcodingchallenge.integration;

import com.crewmeister.cmcodingchallenge.integration.dto.*;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<? extends CurrencyDto> getAllCurrencies(CurrencyRequest request);

    List<String> getAvailableCurrencies(CurrencyRequest request);

    List<? extends ExchangeDto> getExchangeRates(ExchangeRequest request);
}
