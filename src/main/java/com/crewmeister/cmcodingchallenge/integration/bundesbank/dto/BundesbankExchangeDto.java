package com.crewmeister.cmcodingchallenge.integration.bundesbank.dto;

import com.crewmeister.cmcodingchallenge.integration.ExchangeDto;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BundesbankExchangeDto implements ExchangeDto {
    LocalDate date;
    List<ExchangeRateDto> rates;
}
