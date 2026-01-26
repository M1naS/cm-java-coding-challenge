package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.exception.AppException;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateProvider;
import com.crewmeister.cmcodingchallenge.integration.dto.BundesbankCurrencyRequest;
import com.crewmeister.cmcodingchallenge.integration.dto.CurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.dto.CurrencyRequest;
import com.crewmeister.cmcodingchallenge.network.AppResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/api/v1/bundesbank")
@RequiredArgsConstructor
public class BundesbankExchangeRateController {

    private final Map<String, ExchangeRateProvider> providers;

    @GetMapping("/currencies")
    public ResponseEntity<AppResponse<List<? extends CurrencyDto>>> getCurrencies(
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
                providers.get(provider).getCurrencies(currencyRequest),
                HttpStatus.OK.value()
        );
        return new ResponseEntity<>(
                currenciesAppResponse,
                HttpStatus.OK
        );
    }
}
