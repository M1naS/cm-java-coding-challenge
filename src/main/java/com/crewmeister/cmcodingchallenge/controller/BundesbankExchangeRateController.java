package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.exception.AppException;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateProvider;
import com.crewmeister.cmcodingchallenge.integration.dto.*;
import com.crewmeister.cmcodingchallenge.network.AppResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/api/v1/bundesbank")
@RequiredArgsConstructor
public class BundesbankExchangeRateController {

    private final Map<String, ExchangeRateProvider> providers;

    @GetMapping("/all/currencies")
    public ResponseEntity<AppResponse<List<? extends CurrencyDto>>> getAllCurrencies(
            @RequestParam(required = false, defaultValue = "de") String lang,
            @RequestParam(required = false, defaultValue = "bundesbank") String provider
    ) {
        if (providers.get(provider) == null) {
            throw new AppException("Provider not found", HttpStatus.NOT_FOUND);
        }

        CurrencyRequest currencyRequest = null;
        if (provider.equals("bundesbank")) {
            currencyRequest = new BundesbankCurrencyRequest(lang);
        }

        AppResponse<List<? extends CurrencyDto>> currenciesAppResponse = new AppResponse<>(
                providers.get(provider).getAllCurrencies(currencyRequest),
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(
                currenciesAppResponse,
                HttpStatus.OK
        );
    }

    @GetMapping("/available/currencies")
    public ResponseEntity<AppResponse<JsonNode>> getAvailableCurrencies(
            @RequestParam(required = false, defaultValue = "de") String lang,
            @RequestParam(required = false, defaultValue = "bundesbank") String provider
    ) {
        if (providers.get(provider) == null) {
            throw new AppException("Provider not found", HttpStatus.NOT_FOUND);
        }

        CurrencyRequest currencyRequest = null;
        if (provider.equals("bundesbank")) {
            currencyRequest = new BundesbankCurrencyRequest(lang);
        }

        AppResponse<JsonNode> currenciesAppResponse = new AppResponse<>(
                providers.get(provider).getAvailableCurrencies(currencyRequest),
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(
                currenciesAppResponse,
                HttpStatus.OK
        );
    }

    @GetMapping("/exchange-rates")
    public ResponseEntity<AppResponse<JsonNode>> getExchangeRates(
            @RequestParam(required = false, defaultValue = "1") Integer lastNObservations,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false, defaultValue = "bundesbank") String provider
    ) {
        if (providers.get(provider) == null) {
            throw new AppException("Provider not found", HttpStatus.NOT_FOUND);
        }

        ExchangeRequest exchangeRequest = null;
        if (provider.equals("bundesbank")) {
            exchangeRequest = new BundesbankExchangeRequest(lastNObservations,  date);
        } else if (provider.equals("local")) {
            exchangeRequest = new LocalExchangeRequest();
        }

        AppResponse<JsonNode> exchangeAppResponse;

        if (date == null) {
            exchangeAppResponse = new AppResponse<>(
                    providers.get(provider).getExchangeRates(exchangeRequest),
                    HttpStatus.OK.value()
            );
        } else {
            exchangeAppResponse = new AppResponse<>(
                    providers.get(provider).getExchangeRatesByDate(exchangeRequest),
                    HttpStatus.OK.value()
            );
        }

        if (exchangeAppResponse.getBody() != null) {
            return new ResponseEntity<>(
                    exchangeAppResponse,
                    HttpStatus.OK
            );
        }
        throw new AppException("Exchange rates not found", HttpStatus.NOT_FOUND);
    }
}
