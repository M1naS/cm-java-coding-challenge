package com.crewmeister.cmcodingchallenge.integration.bundesbank;

import com.crewmeister.cmcodingchallenge.config.BundesbankProperties;
import com.crewmeister.cmcodingchallenge.exception.AppException;
import com.crewmeister.cmcodingchallenge.exception.BundesbankExchangeRateException;
import com.crewmeister.cmcodingchallenge.integration.*;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.*;
import com.crewmeister.cmcodingchallenge.network.HttpGateway;
import com.crewmeister.cmcodingchallenge.network.AppRequest;
import com.crewmeister.cmcodingchallenge.network.impl.RestTemplateGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class BundesbankExchangeRateProvider implements ExchangeRateProvider {

    private final RestTemplate restTemplate;
    private final BundesbankProperties bundesbankProperties;
    private final BundesbankMapper bundesbankMapper;

    @Override
    public String getProviderName() {
        return "Bundesbank Daily Exchange Rates";
    }


    public List<BundesbankCodelistCurrencyDto> getAllCurrencies(
            CurrencyRequest currencyRequest
    ) {
        List<BundesbankCodelistCurrencyDto> currencyList;

        URI uri = UriComponentsBuilder
                .fromUri(URI.create(bundesbankProperties.getBaseUrl()))
                .path(bundesbankProperties.getSpecifiedCodelistPath())
                .queryParam("lang", ((BundesbankCodelistCurrencyRequest) currencyRequest).getLang())
                .queryParam("format", "struct_json")
                .buildAndExpand("CL_BBK_STD_CURRENCY")
                .toUri();

        HttpGateway restTemplateGateway = new RestTemplateGateway(restTemplate);

        AppRequest appRequest = AppRequest.builder()
                .method(AppRequest.HttpMethod.GET)
                .url(uri.toASCIIString())
                .build();

        log.info("Getting all currency data from {}", getProviderName());

        try {
            currencyList = restTemplateGateway.send(appRequest, BundesbankCodelistCurrencyListDto.class).getBody();
        } catch (RestClientResponseException restClientResponseException) {
            throw new BundesbankExchangeRateException(
                    restClientResponseException.getResponseBodyAsString(),
                    restClientResponseException.getCause()
            );
        } catch (ResourceAccessException resourceAccessException) {
            throw new AppException(
                    resourceAccessException.getMessage(),
                    resourceAccessException.getCause(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        return currencyList;
    }

    @Override
    public List<String> getAvailableCurrencies(
            ExchangeRequest exchangeRequest
    ) {
        URI uri = UriComponentsBuilder
                .fromUri(URI.create(bundesbankProperties.getBaseUrl()))
                .path(bundesbankProperties.getDataPath())
                .queryParam("lang", "en")
                .queryParam("format", "sdmx_csv")
                .queryParam("detail", "dataonly")
                .queryParam("lastNObservations", 1)
                .buildAndExpand(
                        Map.of(
                                "flowRef", "BBEX3",
                                "key", "D..EUR.BB.AC.000"
                        )
                )
                .toUri();

        HttpGateway restTemplateGateway = new RestTemplateGateway(restTemplate);

        AppRequest appRequest = AppRequest.builder()
                .method(AppRequest.HttpMethod.GET)
                .url(uri.toASCIIString())
                .build();

        log.info("Getting currencies from {}", getProviderName());

        try {
            return restTemplateGateway.sendAndExtract(
                    appRequest,
                    null,
                    bundesbankMapper::parseToAvailableCurrencies
            );
        } catch (RestClientResponseException restClientResponseException) {
            throw new BundesbankExchangeRateException(
                    restClientResponseException.getResponseBodyAsString(),
                    restClientResponseException.getCause()
            );
        } catch (ResourceAccessException resourceAccessException) {
            throw new AppException(
                    resourceAccessException.getMessage(),
                    resourceAccessException.getCause(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public List<BundesbankExchangeDto> getExchangeRates(
            ExchangeRequest exchangeRequest
    ) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(
                URI.create(bundesbankProperties.getBaseUrl())
        );

        Optional<Integer> apiLimit = Optional.ofNullable(bundesbankProperties.getDataPathApiLimit());
        if (apiLimit.isPresent() && apiLimit.get() >= 0) {
            uriComponentsBuilder.queryParam(
                    "lastNObservations",
                    bundesbankProperties.getDataPathApiLimit()
            );
        }

        URI uri = uriComponentsBuilder
                .path(bundesbankProperties.getDataPath())
                .queryParam("lang", "en")
                .queryParam("format", "sdmx_csv")
                .queryParam("detail", "dataonly")
                .buildAndExpand(
                        Map.of(
                                "flowRef", "BBEX3",
                                "key", "D..EUR.BB.AC.000"
                        )
                )
                .toUri();

        HttpGateway restTemplateGateway = new RestTemplateGateway(restTemplate);

        AppRequest appRequest = AppRequest.builder()
                .method(AppRequest.HttpMethod.GET)
                .url(uri.toASCIIString())
                .build();

        log.info("Getting exchange rates from {}", getProviderName());

        try {
            return restTemplateGateway.sendAndExtract(
                    appRequest,
                    null,
                    bundesbankMapper::parseToExchangeRateList
            );
        } catch (RestClientResponseException restClientResponseException) {
            throw new BundesbankExchangeRateException(
                    restClientResponseException.getResponseBodyAsString(),
                    restClientResponseException.getCause()
            );
        } catch (ResourceAccessException resourceAccessException) {
            throw new AppException(
                    resourceAccessException.getMessage(),
                    resourceAccessException.getCause(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public BundesbankExchangeDto getExchangeRatesByDate(
            ExchangeRequest exchangeRequest
    ) {
        URI uri = UriComponentsBuilder
                .fromUri(URI.create(bundesbankProperties.getBaseUrl()))
                .path(bundesbankProperties.getDataPath())
                .queryParam("lang", "en")
                .queryParam("format", "sdmx_csv")
                .queryParam("detail", "dataonly")
                .queryParam("lastNObservations", 1)
                .queryParam("endPeriod", exchangeRequest.getDate().toString())
                .buildAndExpand(
                        Map.of(
                                "flowRef", "BBEX3",
                                "key", "D..EUR.BB.AC.000"
                        )
                )
                .toUri();

        HttpGateway restTemplateGateway = new RestTemplateGateway(restTemplate);

        AppRequest appRequest = AppRequest.builder()
                .method(AppRequest.HttpMethod.GET)
                .url(uri.toASCIIString())
                .build();

        log.info("Getting exchange rates of {} from {}", exchangeRequest.getDate().toString(), getProviderName());

        try {
            return restTemplateGateway.sendAndExtract(
                    appRequest,
                    null,
                    bundesbankMapper::parseToExchangeRate
            );
        } catch (RestClientResponseException restClientResponseException) {
            throw new BundesbankExchangeRateException(
                    restClientResponseException.getResponseBodyAsString(),
                    restClientResponseException.getCause()
            );
        } catch (ResourceAccessException resourceAccessException) {
            throw new AppException(
                    resourceAccessException.getMessage(),
                    resourceAccessException.getCause(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
