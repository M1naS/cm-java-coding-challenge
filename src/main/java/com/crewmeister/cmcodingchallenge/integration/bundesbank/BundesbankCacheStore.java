package com.crewmeister.cmcodingchallenge.integration.bundesbank;

import com.crewmeister.cmcodingchallenge.integration.ExchangeDto;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class BundesbankCacheStore {
    private final CacheManager cacheManager;

    public void putByDate(String cacheName, LocalDate date, List<ExchangeRateDto> rates) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(date.toString(), rates);
            log.info("Cached rates for {}", date);
        } else {
            log.warn("Cache {} not found", cacheName);
        }
    }

    public ExchangeDto getByDate(String cacheName, LocalDate date) {
        ExchangeDto bundesbankExchange = null;
        Cache cache = cacheManager.getCache(cacheName);
        Cache.ValueWrapper wrapper;
        if (cache != null) {
            wrapper = cache.get(date.toString());
            if (wrapper != null) {
                log.info("Found exchange rates in cache for {}", date);
                @SuppressWarnings("unchecked")
                List<ExchangeRateDto> exchangeRateList = (List<ExchangeRateDto>) wrapper.get();
                bundesbankExchange = new BundesbankExchangeDto(date, exchangeRateList);
            } else {
                log.warn("Could not find exchange rates in cache for {}", date);
            }
        } else  {
            log.warn("Cache {} not found", cacheName);
        }
        return bundesbankExchange;
    }
}
