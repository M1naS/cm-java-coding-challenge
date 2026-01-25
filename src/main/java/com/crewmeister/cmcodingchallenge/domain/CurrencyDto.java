package com.crewmeister.cmcodingchallenge.domain;

import lombok.Data;

@Data
public abstract class CurrencyDto {
    private String code;
    private String name;
}
