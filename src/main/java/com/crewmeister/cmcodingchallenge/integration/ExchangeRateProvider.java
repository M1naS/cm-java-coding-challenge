package com.crewmeister.cmcodingchallenge.integration;

import com.crewmeister.cmcodingchallenge.integration.dto.CurrencyDto;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<CurrencyDto> getCurrencies();
}
