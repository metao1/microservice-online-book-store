package com.metao.book.product.infrastructure.factory.handler;

import java.time.Instant;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.metao.book.product.application.dto.ProductDTO;
import com.metao.book.product.domain.event.CreateProductEvent;
import com.metao.book.shared.Currency;
import com.metao.book.shared.ProductEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductKafkaHandler implements MessageHandler<CreateProductEvent> {

    private final RemoteProductService remoteProductService;

    @Override
    public void onMessage(@NonNull CreateProductEvent event) {
        try {
            log.info("sending product  to kafka on: {}", event.occurredOn());
            var productDto = event.productDTO();
            Optional.of(productDto)
                    .map(this::mapToProductEvent)
                    .ifPresent(remoteProductService::handle);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
    }

    ProductEvent mapToProductEvent(ProductDTO productDTO) {
        return ProductEvent.newBuilder()
                .setProductId(productDTO.getIsin())
                .setTitle(productDTO.getTitle())
                .setDescription(productDTO.getDescription())
                .setCurrency(mapCurrency(productDTO.getCurrency()))
                .setPrice(productDTO.getPrice().doubleValue())
                .setImageUrl(productDTO.getImageUrl())
                .setCreatedOn(Instant.now().toEpochMilli())
                .build();
    }

    private Currency mapCurrency(com.metao.book.shared.domain.financial.Currency currency) {
        return switch (currency) {
            case DLR -> Currency.dlr;
            case EUR -> Currency.eur;
        };
    }
}
