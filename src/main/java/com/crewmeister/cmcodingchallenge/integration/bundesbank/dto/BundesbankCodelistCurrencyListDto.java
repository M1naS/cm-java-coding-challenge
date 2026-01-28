package com.crewmeister.cmcodingchallenge.integration.bundesbank.dto;


import com.crewmeister.cmcodingchallenge.exception.SerializationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;

@JsonDeserialize(using = BundesbankCodelistCurrencyListDto.Deserializer.class)
public class BundesbankCodelistCurrencyListDto extends ArrayList<BundesbankCodelistCurrencyDto> {
    public static class Deserializer extends StdDeserializer<BundesbankCodelistCurrencyListDto> {
        public Deserializer() { super(BundesbankCodelistCurrencyListDto.class); }
        @Override
        public BundesbankCodelistCurrencyListDto deserialize(JsonParser jsonParser, DeserializationContext context) {
            BundesbankCodelistCurrencyListDto currencyList = new BundesbankCodelistCurrencyListDto();
            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

            try {
                JsonNode root = mapper.readTree(jsonParser);
                JsonNode currencyNodeArray = root.at("/data/codeLists/0/codes");

                if (currencyNodeArray.isArray()) {
                    for (JsonNode currencyNode : currencyNodeArray) {
                        if (!(
                                currencyNode.get("id").textValue().startsWith("_") ||
                                currencyNode.get("id").textValue().chars().anyMatch(Character::isDigit)
                        )) {
                            currencyList.add(
                                    mapper.treeToValue(currencyNode, BundesbankCodelistCurrencyDto.class)
                            );
                        }
                    }
                }
            } catch (IOException ioException) {
                throw new SerializationException("Could not deserialize currency list", ioException);
            }

            return currencyList;
        }
    }
}