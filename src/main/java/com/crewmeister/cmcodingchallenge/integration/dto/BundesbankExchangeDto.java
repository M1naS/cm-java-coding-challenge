package com.crewmeister.cmcodingchallenge.integration.dto;

import com.crewmeister.cmcodingchallenge.exception.SerializationException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = BundesbankExchangeDto.Deserializer.class)
public class BundesbankExchangeDto extends ExchangeDto {
    public static class Deserializer extends StdDeserializer<BundesbankExchangeDto> {
        public Deserializer() {
            super(BundesbankExchangeDto.class);
        }

        @Override
        public BundesbankExchangeDto deserialize(JsonParser jsonParser, DeserializationContext context) {
            BundesbankExchangeDto exchange = new BundesbankExchangeDto();
            if (jsonParser instanceof CsvParser) {
                CsvParser csvParser = (CsvParser) jsonParser;

                CsvMapper mapper = (CsvMapper) csvParser.getCodec();
                try {
                    JsonNode node = mapper.readTree(jsonParser);

                    exchange.setDate(LocalDate.parse(node.get("TIME_PERIOD").asText()));
                } catch (IOException ioException) {
                    throw new SerializationException("Could not deserialize exchange rates list", ioException);
                }
            }
            return exchange;
        }
    }
}
