package com.crewmeister.cmcodingchallenge.integration.local;

import com.crewmeister.cmcodingchallenge.exception.SerializationException;
import com.crewmeister.cmcodingchallenge.integration.*;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankMapper;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankConvertedCurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import com.crewmeister.cmcodingchallenge.integration.local.dto.LocalExchangeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public List<? extends CurrencyDto> getAllCurrencies(CurrencyRequest currencyRequest) {
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
    public List<? extends ExchangeDto> getExchangeRates(ExchangeRequest exchangeRequest) {
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
        BundesbankExchangeDto bundesbankExchange;

        Resource resource = resourceLoader.getResource("classpath:payloads/exchange-data.csv");
        try {
            bundesbankExchange = bundesbankMapper.parseToExchangeRate(resource.getInputStream());
        } catch (IOException ioException) {
            throw new SerializationException("Could not deserialize exchange rates list", ioException);
        }

        return bundesbankExchange;
    }

    @Override
    public BundesbankConvertedCurrencyDto getConvertedForeignExchangeAmount(ExchangeRequest exchangeRequest) {
        BundesbankConvertedCurrencyDto bundesbankConvertedCurrency;
        LocalExchangeRequest localExchangeRequest = (LocalExchangeRequest) exchangeRequest;

        ExchangeDto exchangeRatesByDate = getExchangeRatesByDate(
                LocalExchangeRequest.builder()
                        .lastNObservations(1)
                        .date(localExchangeRequest.getDate())
                        .build()
        );

        BigDecimal returnedRate = new BigDecimal(
                String.valueOf(
                        exchangeRatesByDate.getRates().stream()
                                .filter(
                                        rate -> rate.getCode()
                                                .equals(localExchangeRequest.getCurrencyCode())
                                )
                                .findFirst().map(ExchangeRateDto::getRate)
                                .orElse(new BigDecimal(0))
                )
        );

        bundesbankConvertedCurrency = BundesbankConvertedCurrencyDto.builder()
                .date(exchangeRatesByDate.getDate())
                .rate(returnedRate)
                .currencyCode(localExchangeRequest.getCurrencyCode())
                .amount(localExchangeRequest.getAmount())
                .converted(
                        localExchangeRequest.getAmount().divide(returnedRate, 2, RoundingMode.HALF_UP)
                ).build();

        return bundesbankConvertedCurrency;
    }
}