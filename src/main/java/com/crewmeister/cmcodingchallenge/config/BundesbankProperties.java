package com.crewmeister.cmcodingchallenge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "external.api.bundesbank")
public class BundesbankProperties {
    private String baseUrl;
    private String specifiedCodelistPath;
    private String specifiedCodelistFormat;
    private String dataPath;
    private String dataFormat;
}