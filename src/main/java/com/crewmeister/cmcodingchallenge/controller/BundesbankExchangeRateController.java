package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.exception.AppException;
import com.crewmeister.cmcodingchallenge.integration.*;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankCodelistCurrencyRequest;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankConvertedCurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeRequest;
import com.crewmeister.cmcodingchallenge.integration.local.dto.LocalExchangeRequest;
import com.crewmeister.cmcodingchallenge.network.AppResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            currencyRequest = new BundesbankCodelistCurrencyRequest(lang);
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
    public ResponseEntity<AppResponse<List<String>>> getAvailableCurrencies(
            @RequestParam(required = false, defaultValue = "bundesbank") String provider
    ) {
        if (providers.get(provider) == null) {
            throw new AppException("Provider not found", HttpStatus.NOT_FOUND);
        }

        ExchangeRequest exchangeRequest;
        if (provider.equals("local")) {
            exchangeRequest = LocalExchangeRequest.builder().build();
        } else {
            exchangeRequest = BundesbankExchangeRequest.builder().build();
        }

        AppResponse<List<String>> currenciesAppResponse = new AppResponse<>(
                providers.get(provider).getAvailableCurrencies(exchangeRequest),
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(
                currenciesAppResponse,
                HttpStatus.OK
        );
    }

    @GetMapping("/exchange-rates")
    public ResponseEntity<AppResponse<List<? extends ExchangeDto>>> getExchangeRates(
            @RequestParam(required = false, defaultValue = "1") Integer lastNObservations,
            @RequestParam(required = false, defaultValue = "bundesbank") String provider
    ) {
        if (providers.get(provider) == null) {
            throw new AppException("Provider not found", HttpStatus.NOT_FOUND);
        }

        ExchangeRequest exchangeRequest;
         if (provider.equals("local")) {
            exchangeRequest = LocalExchangeRequest.builder().build();
        } else {
            exchangeRequest = BundesbankExchangeRequest.builder()
                    .lastNObservations(lastNObservations)
                    .build();
        }

        AppResponse<List<? extends ExchangeDto>> exchangeAppResponse;

        exchangeAppResponse = new AppResponse<>(
                providers.get(provider).getExchangeRates(exchangeRequest),
                HttpStatus.OK.value()
        );

        if (exchangeAppResponse.getBody() != null) {
            return new ResponseEntity<>(
                    exchangeAppResponse,
                    HttpStatus.OK
            );
        }
        throw new AppException("Exchange rates not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping(path = "/exchange-rates",  params = "date")
    public ResponseEntity<AppResponse<ExchangeDto>> getExchangeRatesByDate(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false, defaultValue = "bundesbank") String provider
    ) {
        if (providers.get(provider) == null) {
            throw new AppException("Provider not found", HttpStatus.NOT_FOUND);
        }

        ExchangeRequest exchangeRequest;
        if (provider.equals("local")) {
            exchangeRequest = LocalExchangeRequest.builder().build();
        } else {
            exchangeRequest = BundesbankExchangeRequest.builder()
                    .date(date)
                    .build();
        }

        AppResponse<ExchangeDto> exchangeAppResponse;

        exchangeAppResponse = new AppResponse<>(
                providers.get(provider).getExchangeRatesByDate(exchangeRequest),
                HttpStatus.OK.value()
        );

        if (exchangeAppResponse.getBody() != null) {
            return new ResponseEntity<>(
                    exchangeAppResponse,
                    HttpStatus.OK
            );
        }
        throw new AppException("Exchange rates not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/convert")
    public ResponseEntity<AppResponse<BundesbankConvertedCurrencyDto>> getConvertedForeignExchangeAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String currencyCode,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false, defaultValue = "bundesbank") String provider
    ) {
        if (providers.get(provider) == null) {
            throw new AppException("Provider not found", HttpStatus.NOT_FOUND);
        }

        ExchangeRequest exchangeRequest;
        if (provider.equals("local")) {
            exchangeRequest = LocalExchangeRequest.builder()
                    .date(date)
                    .currencyCode(currencyCode)
                    .amount(amount)
                    .build();
        } else {
            exchangeRequest = BundesbankExchangeRequest.builder()
                    .date(date)
                    .currencyCode(currencyCode)
                    .amount(amount)
                    .build();
        }

        AppResponse<BundesbankConvertedCurrencyDto> exchangeAppResponse = new AppResponse<>(
                providers.get(provider).getConvertedForeignExchangeAmount(exchangeRequest),
                HttpStatus.OK.value()
        );

        if (exchangeAppResponse.getBody() != null) {
            return new ResponseEntity<>(
                    exchangeAppResponse,
                    HttpStatus.OK
            );
        }

        throw new AppException("Exchange rates not found", HttpStatus.NOT_FOUND);
    }
}
