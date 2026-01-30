package com.crewmeister.cmcodingchallenge.integration.bundesbank;

import com.crewmeister.cmcodingchallenge.integration.ExchangeDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class BundesbankCacheManager {
    private final BundesbankExchangeRateProvider bundesbankExchangeRateProvider;
    private final CacheManager cacheManager;

    public void clearCache() {
        log.info("Clearing cache..");

        Cache cache = cacheManager.getCache("bundesbank-rates");
        if (cache != null) {
            cache.clear();
            log.info("Cache Cleared!");
        } else {
            log.info("Could not clear cache");
        }
    }

    public void warmingCache() {
        log.info("Warming up the cache...");

        Cache cache = cacheManager.getCache("bundesbank-rates");

        if (cache != null) {
            List<? extends ExchangeDto> exchangeRates = bundesbankExchangeRateProvider.getExchangeRates(
                            BundesbankExchangeRequest.builder().build()
            );

            for (ExchangeDto exchange : exchangeRates) {
                BundesbankExchangeDto bundesbankExchange = (BundesbankExchangeDto) exchange;
                cache.put(bundesbankExchange.getDate(), bundesbankExchange.getRates());
            }
        }

        log.info("Cache warmed!");
    }
}
