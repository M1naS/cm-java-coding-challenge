package com.crewmeister.cmcodingchallenge.config;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CsvConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        CsvMapper csvMapper = new CsvMapper();

        MappingJackson2HttpMessageConverter csvConverter = new MappingJackson2HttpMessageConverter(csvMapper);

        csvConverter.setSupportedMediaTypes(
                List.of(new MediaType("application", "vnd.bbk.data+csv"))
        );

        converters.add(csvConverter);
    }
}