package com.crewmeister.cmcodingchallenge.bootstrap;

import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheWarmer implements CommandLineRunner {

    private final BundesbankCacheService bundesbankCacheService;

    @Override
    public void run(String... args) {
        bundesbankCacheService.warmingCache();
    }
}