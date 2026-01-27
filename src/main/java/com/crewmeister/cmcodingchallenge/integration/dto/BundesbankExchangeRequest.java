package com.crewmeister.cmcodingchallenge.integration.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@Builder
public class BundesbankExchangeRequest implements ExchangeRequest {
    private final int lastNObservations;
    private final LocalDate date;
    private final String currencyCode;
    private final BigDecimal amount;
}
