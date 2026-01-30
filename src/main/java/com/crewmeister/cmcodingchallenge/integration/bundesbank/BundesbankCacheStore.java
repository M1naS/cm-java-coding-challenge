package com.crewmeister.cmcodingchallenge.integration.bundesbank;

import com.crewmeister.cmcodingchallenge.integration.ExchangeDto;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class BundesbankCacheStore {
    private final CacheManager cacheManager;

    @Value("${application.cache.rates-name}")
    private Optional<String> ratesCacheName;

    public void putByDate(LocalDate date, List<ExchangeRateDto> rates) {
        Cache cache = cacheManager.getCache(ratesCacheName.orElse("rates"));
        if (cache != null) {
            cache.put(date, rates);
            log.info("Cached rates for {}", date);
        } else {
            log.warn("Cache rates not found");
        }
    }

    public BundesbankExchangeDto getByDate(LocalDate date) {
        BundesbankExchangeDto bundesbankExchange = null;
        Cache cache = cacheManager.getCache(ratesCacheName.orElse("rates"));
        Cache.ValueWrapper wrapper;
        if (cache != null) {
            wrapper = cache.get(date);
            if (wrapper != null) {
                log.info("Found exchange rates in cache for {}", date);
                @SuppressWarnings("unchecked")
                List<ExchangeRateDto> exchangeRateList = (List<ExchangeRateDto>) wrapper.get();
                bundesbankExchange = new BundesbankExchangeDto(date, exchangeRateList);
            } else {
                log.warn("Could not find exchange rates in cache for {}", date);
            }
        } else  {
            log.warn("Cache rates not found");
        }
        return bundesbankExchange;
    }

    public List<BundesbankExchangeDto> getAll() {
        Cache cache = cacheManager.getCache(ratesCacheName.orElse("rates"));

        if (cache != null) {
            @SuppressWarnings("unchecked")
            com.github.benmanes.caffeine.cache.Cache<LocalDate, List<ExchangeRateDto>> caffeineCache =
                    (com.github.benmanes.caffeine.cache.Cache<LocalDate, List<ExchangeRateDto>>)
                            cache.getNativeCache();


            Set<Map.Entry<LocalDate, List<ExchangeRateDto>>> exchangesSet = caffeineCache.asMap().entrySet();
            BundesbankExchangeDto[] exchanges = new BundesbankExchangeDto[exchangesSet.size()];

            int i = 0;
            for (Map.Entry<LocalDate, List<ExchangeRateDto>> exchange : exchangesSet) {
                exchanges[i] = new BundesbankExchangeDto(exchange.getKey(), exchange.getValue());
                i++;
            }

            Arrays.parallelSort(exchanges,
                    Comparator.comparingLong(
                            (ExchangeDto exchange) -> exchange.getDate().toEpochDay())
                            .reversed()
            );

            return Arrays.asList(exchanges);
        } else  {
            log.warn("Cache rates not found");
        }
        return new ArrayList<>();
    }
}
