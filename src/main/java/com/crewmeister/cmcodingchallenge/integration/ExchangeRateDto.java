package com.crewmeister.cmcodingchallenge.integration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeRateDto {
    String code;
    BigDecimal rate;
}
