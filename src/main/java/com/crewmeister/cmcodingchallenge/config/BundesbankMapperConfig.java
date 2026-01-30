package com.crewmeister.cmcodingchallenge.config;

import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BundesbankMapperConfig {
    @Bean
    public BundesbankMapper bundesbankMapper() {
        AfterburnerModule afterburnerModule = new AfterburnerModule();

        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.SKIP_EMPTY_LINES);
        csvMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        csvMapper.registerModule(afterburnerModule);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        objectMapper.registerModule(afterburnerModule);

        return new BundesbankMapper(objectMapper,  csvMapper);
    }
}
