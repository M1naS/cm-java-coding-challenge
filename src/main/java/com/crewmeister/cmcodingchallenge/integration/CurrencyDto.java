package com.crewmeister.cmcodingchallenge.integration;

import lombok.Data;

@Data
public abstract class CurrencyDto {
    private String code;
    private String name;
}
