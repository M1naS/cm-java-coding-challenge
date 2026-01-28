package com.crewmeister.cmcodingchallenge.integration.bundesbank.dto;

import com.crewmeister.cmcodingchallenge.integration.ExchangeRequest;
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
