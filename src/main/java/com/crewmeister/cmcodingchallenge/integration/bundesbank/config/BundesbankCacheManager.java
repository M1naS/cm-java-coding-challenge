package com.crewmeister.cmcodingchallenge.integration.bundesbank.config;

import com.crewmeister.cmcodingchallenge.config.ICacheManager;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankExchangeRateService;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BundesbankCacheManager implements ICacheManager {
    private final CacheManager cacheManager;
    private final BundesbankExchangeRateService bundesbankExchangeRateService;

    @Value("${application.cache.rates-name}")
    private Optional<String> ratesCacheName;

    @Override
    public void clearCache() {
        log.info("Clearing cache..");

        Cache cache = cacheManager.getCache(ratesCacheName.orElse("rates"));
        if (cache != null) {
            cache.clear();
            log.info("Cache Cleared!");
        } else {
            log.info("Could not clear cache");
        }
    }

    @Override
    public void warmingCache() {
        log.info("Warming up the cache...");

        Cache cache = cacheManager.getCache(ratesCacheName.orElse("rates"));

        if (cache != null) {
            List<BundesbankExchangeDto> exchangeRates = bundesbankExchangeRateService.getExchangeRates();

            for (BundesbankExchangeDto exchange : exchangeRates) {
                cache.put(exchange.getDate(), exchange.getRates());
            }
        }

        log.info("Cache warmed!");
    }
}
