package com.crewmeister.cmcodingchallenge.integration;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeDto {
    LocalDate getDate();
    List<ExchangeRateDto> getRates();
}
