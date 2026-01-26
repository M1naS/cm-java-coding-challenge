package com.crewmeister.cmcodingchallenge.integration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public abstract class ExchangeDto {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private List<ExchangeRateDto> rates;
}
