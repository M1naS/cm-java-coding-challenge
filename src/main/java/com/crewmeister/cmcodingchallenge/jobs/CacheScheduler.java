package com.crewmeister.cmcodingchallenge.jobs;

import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class CacheScheduler {
    BundesbankCacheService bundesbankCacheService;

    @Scheduled(cron = "0 30 16 * * MON-FRI", zone = "CET")
    public void refreshCache() {
        log.info("Clearing cache..");
        bundesbankCacheService.clearCache();
        log.info("Cache Cleared!");
        bundesbankCacheService.warmingCache();
    }
}