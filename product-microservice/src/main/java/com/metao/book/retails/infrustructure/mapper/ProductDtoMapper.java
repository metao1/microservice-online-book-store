package com.metao.book.retails.infrustructure.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metao.book.retails.application.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDtoMapper implements DTOMapper<String, Optional<ProductDTO>> {

    private final ObjectMapper mapper;

    @Override
    public Optional<ProductDTO> convertToDto(String productString) {
        final ProductDTO productDTO;
        try {
            productDTO = mapper.readValue(productString, ProductDTO.class);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
            return Optional.empty();
        }
        return Optional.ofNullable(productDTO);
    }

}
