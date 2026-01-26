package com.crewmeister.cmcodingchallenge.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    private String code;
    private BigDecimal rate;
}
