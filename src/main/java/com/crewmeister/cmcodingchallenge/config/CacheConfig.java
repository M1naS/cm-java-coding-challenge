package com.crewmeister.cmcodingchallenge.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Configuration
@EnableCaching
public class CacheConfig {
    private final BundesbankProperties bundesbankProperties;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Optional<Integer> apiLimit = Optional.ofNullable(bundesbankProperties.getDataPathApiLimit());

        Integer numOfDaysSinceApiWasCreated = calculateWorkingDaysSince("1999-01-01");

        cacheManager.registerCustomCache("bundesbank-rates",
                Caffeine.newBuilder()
                        .initialCapacity(apiLimit.orElse(numOfDaysSinceApiWasCreated))
                        .recordStats()
                        .build());

        cacheManager.registerCustomCache("currencies",
                Caffeine.newBuilder()
                        .initialCapacity(5)
                        .recordStats()
                        .build());

        return cacheManager;
    }

    private Integer calculateWorkingDaysSince(String dateStr) {
        LocalDate startDate = LocalDate.parse(dateStr);
        LocalDate today = LocalDate.now();

        long days = startDate.datesUntil(today)
                .filter(date -> {
                    DayOfWeek day = date.getDayOfWeek();
                    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
                })
                .count();

        return Math.toIntExact(days);
    }
}