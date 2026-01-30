package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.exception.AppException;
import com.crewmeister.cmcodingchallenge.integration.*;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankExchangeRateService;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.*;
import com.crewmeister.cmcodingchallenge.network.AppResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bundesbank")
@RequiredArgsConstructor
public class BundesbankExchangeRateController {
    private final BundesbankExchangeRateService bundesbankExchangeRateService;

    @Cacheable(value = "${application.cache.currencies-name:currencies}", key = "'all_' + #lang", condition = "#lang == 'en' || #lang == 'de'")
    @GetMapping("/all/currencies")
    public ResponseEntity<AppResponse<List<BundesbankCodelistCurrencyDto>>> getAllCurrencies(
            @RequestParam(required = false, defaultValue = "de") String lang
    ) {
        AppResponse<List<BundesbankCodelistCurrencyDto>> currenciesAppResponse = new AppResponse<>(
                bundesbankExchangeRateService.getAllCurrencies(lang),
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(
                currenciesAppResponse,
                HttpStatus.OK
        );
    }

    @Cacheable(value = "${application.cache.currencies-name:currencies}", key = "'available'")
    @GetMapping("/available/currencies")
    public ResponseEntity<AppResponse<List<String>>> getAvailableCurrencies() {
        AppResponse<List<String>> currenciesAppResponse = new AppResponse<>(
                bundesbankExchangeRateService.getAvailableCurrencies(),
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(
                currenciesAppResponse,
                HttpStatus.OK
        );
    }

    @GetMapping("/exchange-rates")
    public ResponseEntity<AppResponse<List<BundesbankExchangeDto>>> getExchangeRates() {
        AppResponse<List<BundesbankExchangeDto>> exchangeAppResponse = new AppResponse<>(
                bundesbankExchangeRateService.getCachedExchangeRates(),
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
            LocalDate date
    ) {
        AppResponse<ExchangeDto> exchangeAppResponse = new AppResponse<>(
                bundesbankExchangeRateService.getExchangeRatesByDate(date),
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
            @RequestParam BigDecimal amount
    ) {
        BundesbankConvertedCurrencyDto bundesbankConvertedCurrency = BundesbankConvertedCurrencyDto.builder()
                .date(date)
//                .rate(returnedRate)
                .currencyCode(currencyCode)
                .amount(amount)
                .converted(
                        bundesbankExchangeRateService.getConvertedForeignExchangeAmount(
                                date,
                                currencyCode,
                                amount
                        )
                ).build();

        AppResponse<BundesbankConvertedCurrencyDto> exchangeAppResponse = new AppResponse<>(
                bundesbankConvertedCurrency,
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
