package com.crewmeister.cmcodingchallenge.integration.dto;

import lombok.Data;

@Data
public abstract class CurrencyDto {
    private String code;
    private String name;
}
