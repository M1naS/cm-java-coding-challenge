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
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service("bundesbank")
@Slf4j
public class BundesbankExchangeRateProvider implements ExchangeRateProvider {

    private final RestTemplate restTemplate;
    private final BundesbankProperties bundesbankProperties;
    private final BundesbankMapper bundesbankMapper;
    private final BundesbankCacheStore bundesbankCacheStore;

    @Override
    public String getProviderName() {
        return "Bundesbank Daily Exchange Rates";
    }


    public List<? extends CurrencyDto> getAllCurrencies(
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
    public List<? extends ExchangeDto> getExchangeRates(
            ExchangeRequest exchangeRequest
    ) {
        URI uri = UriComponentsBuilder
                .fromUri(URI.create(bundesbankProperties.getBaseUrl()))
                .path(bundesbankProperties.getDataPath())
                .queryParam("lang", "en")
                .queryParam("format", "sdmx_csv")
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
    public ExchangeDto getExchangeRatesByDate(
            ExchangeRequest exchangeRequest
    ) {
        URI uri = UriComponentsBuilder
                .fromUri(URI.create(bundesbankProperties.getBaseUrl()))
                .path(bundesbankProperties.getDataPath())
                .queryParam("lang", "en")
                .queryParam("format", "sdmx_csv")
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
            ExchangeDto cachedExchange =  bundesbankCacheStore.getByDate(
                    "bundesbank-rates",
                    exchangeRequest.getDate()
            );

            if (cachedExchange != null) {
                return cachedExchange;
            }

            ExchangeDto returnedExchange = restTemplateGateway.sendAndExtract(
                    appRequest,
                    null,
                    bundesbankMapper::parseToExchangeRate
            );

            bundesbankCacheStore.putByDate(
                    "bundesbank-rates",
                    returnedExchange.getDate(),
                    returnedExchange.getRates()
            );

            return returnedExchange;
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
    public BundesbankConvertedCurrencyDto getConvertedForeignExchangeAmount(
            ExchangeRequest exchangeRequest
    ) {
        BundesbankExchangeRequest bundesbankExchangeRequest = (BundesbankExchangeRequest) exchangeRequest;
        BundesbankConvertedCurrencyDto bundesbankConvertedCurrency;

        ExchangeDto exchangeRatesByDate = getExchangeRatesByDate(
                BundesbankExchangeRequest.builder()
                        .date(bundesbankExchangeRequest.getDate())
                        .build()
        );

        log.info("Converting {}, from {} to EUR for date {} using {}",
                bundesbankExchangeRequest.getAmount(),
                bundesbankExchangeRequest.getCurrencyCode(),
                bundesbankExchangeRequest.getDate().toString(),
                getProviderName()
        );

        if (exchangeRatesByDate == null)
            throw new AppException("Could not get exchange rate", HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            BigDecimal returnedRate = new BigDecimal(
                    String.valueOf(
                            exchangeRatesByDate.getRates().stream()
                                    .filter(
                                            rate -> rate.getCode()
                                            .equals(bundesbankExchangeRequest.getCurrencyCode())
                                    )
                                    .findFirst().map(ExchangeRateDto::getRate)
                                    .orElse(new BigDecimal(0))
                    )
            );

            if (returnedRate.compareTo(BigDecimal.ZERO) == 0) {
                throw new AppException("Exchange rate not found", HttpStatus.BAD_REQUEST);
            }

            bundesbankConvertedCurrency = BundesbankConvertedCurrencyDto.builder()
                    .date(exchangeRatesByDate.getDate())
                    .rate(returnedRate)
                    .currencyCode(bundesbankExchangeRequest.getCurrencyCode())
                    .amount(bundesbankExchangeRequest.getAmount())
                    .converted(
                            bundesbankExchangeRequest.getAmount().divide(returnedRate, 2, RoundingMode.HALF_UP)
                    ).build();

        } catch (IllegalArgumentException illegalArgumentException) {
            throw new  AppException("No support for currency " + bundesbankExchangeRequest.getCurrencyCode(), HttpStatus.BAD_REQUEST);
        }

        return bundesbankConvertedCurrency;
    }
}
