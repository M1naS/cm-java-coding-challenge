package com.crewmeister.cmcodingchallenge.config;

import com.crewmeister.cmcodingchallenge.integration.BundesbankMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BundesbankMapperConfig {
    @Bean
    public BundesbankMapper bundesbankMapper() {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.SKIP_EMPTY_LINES);

        return new BundesbankMapper(csvMapper);
    }
}
