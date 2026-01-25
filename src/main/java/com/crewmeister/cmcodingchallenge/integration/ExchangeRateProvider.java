package com.crewmeister.cmcodingchallenge.integration;

import com.crewmeister.cmcodingchallenge.integration.dto.CurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.dto.CurrencyRequest;

import java.util.List;

public interface ExchangeRateProvider {
    String getProviderName();

    List<? extends CurrencyDto> getCurrencies(CurrencyRequest request);
}
