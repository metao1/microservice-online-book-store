package com.metao.product.infrustructure.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metao.product.infrustructure.factory.handler.FileHandler;

import org.junit.jupiter.api.Test;

class ProductDtoMapperTest {

    @Test
    void givenString_convertToDto_thenIsOk() throws IOException {
        var productDtoMapper = new ProductDtoMapper(new ObjectMapper());
        try(var content = new FileHandler().readFromFile("data/one_product.json")){
            content.forEach(st -> {
                var productDto = productDtoMapper.convertToDto(st);
                assertTrue(productDto.isPresent());
           });
        }
    }
}
