package com.crewmeister.cmcodingchallenge.jobs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheEvictor {
    @Scheduled(cron = "0 15 14 * * *", zone = "Europe/Paris")
    @CacheEvict(value = "bundesbank-rates", allEntries = true)
    public void clearCacheDaily() {
        log.info("Cleared cache {} at 14:15 CET/CEST", "bundesbank-rates");
    }
}