package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.integration.ExchangeRateProvider;
import com.crewmeister.cmcodingchallenge.integration.dto.BundesbankCurrencyRequest;
import com.crewmeister.cmcodingchallenge.integration.dto.CurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.impl.BundesbankExchangeRateProvider;
import com.crewmeister.cmcodingchallenge.network.AppResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/bundesbank")
@RequiredArgsConstructor
public class BundesbankExchangeRateController {

    private final ExchangeRateProvider exchangeRateProvider;

    @GetMapping("/currencies")
    public ResponseEntity<AppResponse<List<? extends CurrencyDto>>> getCurrencies(
            @RequestParam(required = false, defaultValue = "de") String lang
    ) {
        AppResponse<List<? extends CurrencyDto>> currenciesAppResponse = new AppResponse<>(
                ((BundesbankExchangeRateProvider) exchangeRateProvider).getCurrencies(
                        new BundesbankCurrencyRequest(lang)
                ),
                HttpStatus.OK.value()
        );

        return new ResponseEntity<>(
                currenciesAppResponse,
                HttpStatus.OK
        );
    }
}
