package com.crewmeister.cmcodingchallenge.integration.local;

import com.crewmeister.cmcodingchallenge.exception.SerializationException;
import com.crewmeister.cmcodingchallenge.integration.CurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.CurrencyRequest;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRequest;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateProvider;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankMapper;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankConvertedCurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service("local")
@Slf4j
public class LocalExchangeRateProvider implements ExchangeRateProvider {
    private final ResourceLoader resourceLoader;
    private final BundesbankMapper bundesbankMapper;

    @Override
    public String getProviderName() {
        return "Local Provider";
    }

    @Override
    public List<? extends CurrencyDto> getAllCurrencies(CurrencyRequest request) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getAvailableCurrencies(ExchangeRequest exchangeRequest) {
        List<String> currencies;

        Resource resource = resourceLoader.getResource("classpath:payloads/exchange-data.csv");
        try {
            currencies = bundesbankMapper.parseToAvailableCurrencies(resource.getInputStream());
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize exchange rates list", ioException);
        }

        return currencies;
    }

    @Override
    public List<BundesbankExchangeDto> getExchangeRates(ExchangeRequest request) {
        List<BundesbankExchangeDto> bundesbankExchangeList;

        Resource resource = resourceLoader.getResource("classpath:payloads/exchange-data.csv");
        try {
            bundesbankExchangeList = bundesbankMapper.parseToExchangeRateList(resource.getInputStream());
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize exchange rates list", ioException);
        }

        return bundesbankExchangeList;
    }

    @Override
    public BundesbankExchangeDto getExchangeRatesByDate(ExchangeRequest exchangeRequest) {
        return null;
    }

    @Override
    public BundesbankConvertedCurrencyDto getConvertedForeignExchangeAmount(ExchangeRequest request) {
        return null;
    }
}