package com.crewmeister.cmcodingchallenge.bootstrap;

import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheWarmer implements CommandLineRunner {

    private final BundesbankCacheManager bundesbankCacheManager;

    @Override
    public void run(String... args) {
        bundesbankCacheManager.warmingCache();
    }
}