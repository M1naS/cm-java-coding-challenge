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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service("bundesbank")
@Slf4j
public class BundesbankExchangeRateProvider implements ExchangeRateProvider {

    private final BundesbankProperties bundesbankProperties;
    private final RestTemplate restTemplate;
    private final BundesbankMapper bundesbankMapper;
    private final CacheManager cacheManager;

    @Override
    public String getProviderName() {
        return "Bundesbank Daily Exchange Rates";
    }


    public List<? extends CurrencyDto> getAllCurrencies(
            CurrencyRequest currencyRequest
    ) {
        List<BundesbankCodelistCurrencyDto> currencyList;

        String url = UriComponentsBuilder
                .fromUriString(bundesbankProperties.getBaseUrl())
                .path(bundesbankProperties.getSpecifiedCodelistPath())
                .queryParam("lang", ((BundesbankCodelistCurrencyRequest) currencyRequest).getLang())
                .queryParam("format", bundesbankProperties.getSpecifiedCodelistFormat())
                .buildAndExpand("CL_BBK_STD_CURRENCY")
                .toUriString();

        HttpGateway client = new RestTemplateGateway(restTemplate);

        AppRequest request = AppRequest.builder()
                .method(AppRequest.HttpMethod.GET)
                .url(url)
                .build();

        log.info("Getting currencies from {}", getProviderName());

        try {
            currencyList = client.send(request, BundesbankCodelistCurrencyListDto.class).getBody();
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
    public List<String> getAvailableCurrencies(ExchangeRequest exchangeRequest) {
        String url = UriComponentsBuilder
                .fromUriString(bundesbankProperties.getBaseUrl())
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
                .toUriString();

//        HttpGateway client = new RestTemplateGateway(restTemplate);
        RestTemplate restTemplate = new RestTemplate();

//        AppRequest request = AppRequest.builder()
//                .method(AppRequest.HttpMethod.GET)
//                .url(url)
//                .build();

        log.info("Getting currencies from {}", getProviderName());

        try {
//            currencyList = client.send(request, BundesbankDataCurrencyListDto.class).getBody();
            return restTemplate.execute(url,
                    HttpMethod.GET,
                    null,
                    response -> bundesbankMapper.parseToAvailableCurrencies(response.getBody())
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
        String url = UriComponentsBuilder
                .fromUriString(bundesbankProperties.getBaseUrl())
                .path(bundesbankProperties.getDataPath())
                .queryParam("lang", "en")
                .queryParam("format", bundesbankProperties.getDataFormat())
                .queryParam("lastNObservations", exchangeRequest.getLastNObservations())
                .buildAndExpand(
                        Map.of(
                                "flowRef", "BBEX3",
                                "key", "D..EUR.BB.AC.000"
                        )
                )
                .toUriString();

//        HttpGateway client = new RestTemplateGateway(restTemplate);
        RestTemplate restTemplate = new RestTemplate();

//        AppRequest request = AppRequest.builder()
//                .method(AppRequest.HttpMethod.GET)
//                .url(url)
//                .build();

        log.info("Getting exchange rates from {}", getProviderName());

        try {
            return restTemplate.execute(url,
                    HttpMethod.GET,
                    null,
                    response -> bundesbankMapper.parseToExchangeRateList(response.getBody())
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
    public ExchangeDto getExchangeRatesByDate(ExchangeRequest exchangeRequest) {
        String url = UriComponentsBuilder
                .fromUriString(bundesbankProperties.getBaseUrl())
                .path(bundesbankProperties.getDataPath())
                .queryParam("lang", "en")
                .queryParam("format", bundesbankProperties.getDataFormat())
                .queryParam("lastNObservations", 1)
                .queryParam("endPeriod", exchangeRequest.getDate().toString())
                .buildAndExpand(
                        Map.of(
                                "flowRef", "BBEX3",
                                "key", "D..EUR.BB.AC.000"
                        )
                )
                .toUriString();

//        HttpGateway client = new RestTemplateGateway(restTemplate);
        RestTemplate restTemplate = new RestTemplate();

//        AppRequest request = AppRequest.builder()
//                .method(AppRequest.HttpMethod.GET)
//                .url(url)
//                .build();

        log.info("Getting exchange rates of {} from {}", exchangeRequest.getDate().toString(), getProviderName());

        try {
            Cache cache = cacheManager.getCache("bundesbank-rates");
            Cache.ValueWrapper wrapper;
            if (cache != null) {
                wrapper = cache.get(exchangeRequest.getDate().toString());
                if (wrapper != null) {
                    log.info("Found exchange rates in cache for {}", exchangeRequest.getDate().toString());
                    @SuppressWarnings("unchecked")
                    List<ExchangeRateDto> exchangeRateList = (List<ExchangeRateDto>) wrapper.get();

                    return new BundesbankExchangeDto(exchangeRequest.getDate(), exchangeRateList);
                } else {
                    log.warn("Could not find exchange rates in cache for {}", exchangeRequest.getDate().toString());
                }
            }


            return restTemplate.execute(url,
                    HttpMethod.GET,
                    null,
                    response -> bundesbankMapper.parseToExchangeRate(response.getBody())
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
    public BundesbankConvertedCurrencyDto getConvertedForeignExchangeAmount(ExchangeRequest exchangeRequest) {
        BundesbankExchangeRequest bundesbankExchangeRequest = (BundesbankExchangeRequest) exchangeRequest;
        BundesbankConvertedCurrencyDto bundesbankConvertedCurrency;

        ExchangeDto exchangeRatesByDate = getExchangeRatesByDate(
                BundesbankExchangeRequest.builder()
                        .lastNObservations(1)
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
                    .date(bundesbankExchangeRequest.getDate())
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
