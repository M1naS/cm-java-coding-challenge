package com.crewmeister.cmcodingchallenge.integration.bundesbank;

import com.crewmeister.cmcodingchallenge.exception.AppException;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BundesbankExchangeRateService {
    private final BundesbankCacheStore bundesbankCacheStore;
    private final BundesbankExchangeRateProvider bundesbankExchangeRateProvider;

    @Value("${external.api.bundesbank.specified-codelist.non-currencies}")
    private String ignoredCurrencies;

    public List<BundesbankCodelistCurrencyDto> getAllCurrencies(String lang) {

        List<String> ignoredCurrenciesList = Arrays.asList(ignoredCurrencies.split(","));

        return bundesbankExchangeRateProvider.getAllCurrencies(
                new BundesbankCodelistCurrencyRequest(lang)
        ).stream()
                .filter(currency -> !ignoredCurrenciesList.contains(currency.getCode()))
                .collect(Collectors.toList());
    }

    public List<String> getAvailableCurrencies() {
        return bundesbankExchangeRateProvider.getAvailableCurrencies(
                BundesbankExchangeRequest.builder().build()
        );
    }

    public List<BundesbankExchangeDto> getExchangeRates() {
        return bundesbankExchangeRateProvider.getExchangeRates(
                BundesbankExchangeRequest.builder().build()
        );
    }

    public List<BundesbankExchangeDto> getCachedExchangeRates() {
        List<BundesbankExchangeDto> exchangeList = bundesbankCacheStore.getAll();

        if (exchangeList.isEmpty()) {
            return getExchangeRates();
        } else {
            return exchangeList;
        }
    }

    public BundesbankExchangeDto getExchangeRatesByDate(LocalDate date) {
        BundesbankExchangeDto cachedExchange =  bundesbankCacheStore.getByDate(date);
        if (cachedExchange != null) {
            return cachedExchange;
        }

        BundesbankExchangeDto returnedExchange = bundesbankExchangeRateProvider.getExchangeRatesByDate(
                BundesbankExchangeRequest.builder().date(date).build()
        );

        bundesbankCacheStore.putByDate(
                returnedExchange.getDate(),
                returnedExchange.getRates()
        );

        return returnedExchange;
    }

    public BigDecimal getConvertedForeignExchangeAmount(
            LocalDate date,
            String currencyCode,
            BigDecimal amount
    ) {
        BundesbankExchangeDto exchangeRatesByDate = getExchangeRatesByDate(date);

        log.info("Converting {}, from {} to EUR for date {} using {}",
                amount,
                currencyCode,
                date.toString(),
                bundesbankExchangeRateProvider.getProviderName()
        );

        if (exchangeRatesByDate == null)
            throw new AppException("Could not get exchange rate", HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            BigDecimal returnedRate = new BigDecimal(
                    String.valueOf(
                            exchangeRatesByDate.getRates().stream()
                                    .filter(
                                            rate -> rate.getCode()
                                                    .equals(currencyCode)
                                    )
                                    .findFirst().map(ExchangeRateDto::getRate)
                                    .orElse(new BigDecimal(0))
                    )
            );

            if (returnedRate.compareTo(BigDecimal.ZERO) == 0) {
                throw new AppException("Exchange rate not found", HttpStatus.BAD_REQUEST);
            }

            return amount.divide(returnedRate, 2, RoundingMode.HALF_UP);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new AppException("No support for currency " + currencyCode, HttpStatus.BAD_REQUEST);
        }
    }
}
