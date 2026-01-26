package com.crewmeister.cmcodingchallenge.integration.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRateDto {
    private String code;
    private BigDecimal rate;
}
