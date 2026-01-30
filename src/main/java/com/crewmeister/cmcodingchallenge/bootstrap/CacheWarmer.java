package com.crewmeister.cmcodingchallenge.bootstrap;

import com.crewmeister.cmcodingchallenge.config.ICacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheWarmer implements CommandLineRunner {

    private final ICacheManager cacheManager;

    @Override
    public void run(String... args) {
        cacheManager.warmingCache();
    }
}