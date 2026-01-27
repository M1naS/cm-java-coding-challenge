package com.crewmeister.cmcodingchallenge.integration.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class BundesbankExchangeRequest implements ExchangeRequest {
    private final int lastNObservations;
    private final LocalDate date;
}
