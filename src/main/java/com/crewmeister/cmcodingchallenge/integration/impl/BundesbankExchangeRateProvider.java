package com.crewmeister.cmcodingchallenge.integration.impl;

import com.crewmeister.cmcodingchallenge.config.BundesbankProperties;
import com.crewmeister.cmcodingchallenge.exception.AppException;
import com.crewmeister.cmcodingchallenge.exception.BundesbankExchangeRateException;
import com.crewmeister.cmcodingchallenge.integration.dto.BundesbankCurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateProvider;
import com.crewmeister.cmcodingchallenge.integration.dto.BundesbankCurrencyListDto;
import com.crewmeister.cmcodingchallenge.integration.dto.BundesbankCurrencyRequest;
import com.crewmeister.cmcodingchallenge.integration.dto.CurrencyRequest;
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

import java.util.List;

@RequiredArgsConstructor
@Service("bundesbank")
@Slf4j
public class BundesbankExchangeRateProvider implements ExchangeRateProvider {

    private final BundesbankProperties bundesbankProperties;
    private final RestTemplate restTemplate;

    @Override
    public String getProviderName() {
        return "Bundesbank Daily Exchange Rates";
    }

    @Override
    public List<BundesbankCurrencyDto> getCurrencies(
            CurrencyRequest currencyRequest
    ) {
        List<BundesbankCurrencyDto> currencyList;

        String url = UriComponentsBuilder
                .fromUriString(bundesbankProperties.getBaseUrl())
                .path(bundesbankProperties.getSpecifiedCodelistPath())
                .queryParam("lang", ((BundesbankCurrencyRequest) currencyRequest).getLang())
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
            currencyList = client.send(request, BundesbankCurrencyListDto.class).getBody();
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
}
