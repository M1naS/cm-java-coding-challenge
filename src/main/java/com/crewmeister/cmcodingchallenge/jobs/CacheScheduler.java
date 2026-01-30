package com.crewmeister.cmcodingchallenge.jobs;

import com.crewmeister.cmcodingchallenge.integration.bundesbank.config.BundesbankCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class CacheScheduler {
    private final BundesbankCacheManager bundesbankCacheManager;

    @Scheduled(cron = "0 30 16 * * MON-FRI", zone = "CET")
    public void refreshCache() {
        bundesbankCacheManager.clearCache();
        bundesbankCacheManager.warmingCache();
    }
}