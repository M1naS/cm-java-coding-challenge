package com.crewmeister.cmcodingchallenge.integration.local;

import com.crewmeister.cmcodingchallenge.integration.CurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.CurrencyRequest;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRequest;
import com.crewmeister.cmcodingchallenge.integration.ExchangeRateProvider;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.BundesbankMapper;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankConvertedCurrencyDto;
import com.crewmeister.cmcodingchallenge.integration.bundesbank.dto.BundesbankExchangeDto;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service("local")
@Slf4j
public class LocalExchangeRateProvider implements ExchangeRateProvider {
    private final ResourceLoader resourceLoader;
    private final BundesbankMapper bundesbankMapper;

    @Override
    public String getProviderName() {
        return "Local Provider";
    }

    @Override
    public List<? extends CurrencyDto> getAllCurrencies(CurrencyRequest request) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getAvailableCurrencies(ExchangeRequest exchangeRequest) {
        return Collections.emptyList();
    }

    @Override
    public List<BundesbankExchangeDto> getExchangeRates(ExchangeRequest request) {

        Resource resource = resourceLoader.getResource("classpath:payloads/sdmx_csv-dataonly-1.csv");

        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.SKIP_EMPTY_LINES);

        CsvSchema schema = CsvSchema.emptySchema().withHeader();

//        try (
//                MappingIterator<BundesbankExchangeDto> iterator = csvMapper.readerFor(BundesbankExchangeDto.class)
//                                .with(schema)
//                                .readValues(resource.getFile())
//        ) {
//            while (iterator.hasNext()) {
//                    response.add(iterator.next());
//            }
//        } catch (IOException ioException) {
//            throw new SerializationException("Could not deserialize exchange rates list", ioException);
//        }

//        return bundesbankMapper.getJsonMapper().createObjectNode();
        return Collections.emptyList();
    }

    @Override
    public BundesbankExchangeDto getExchangeRatesByDate(ExchangeRequest exchangeRequest) {
        return null;
    }

    @Override
    public BundesbankConvertedCurrencyDto getConvertedForeignExchangeAmount(ExchangeRequest request) {
        return null;
    }
}