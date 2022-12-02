package com.metao.book.product.infrastructure.factory;

import com.metao.book.product.infrastructure.factory.handler.FileHandler;
import com.metao.book.product.infrastructure.factory.handler.LogMessageHandler;
import com.metao.book.product.infrastructure.factory.handler.ProductEventHandler;
import com.metao.book.product.infrastructure.factory.handler.ProductMessageHandler;
import com.metao.book.product.infrastructure.mapper.ProductDtoMapper;
import com.metao.book.product.infrastructure.util.EventUtil;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod")
public class ProductGenerator implements InitializingBean {

    private final ProductMessageHandler productMessageHandler;
    private final LogMessageHandler logMessageHandler;

    private final ProductDtoMapper mapper;
    private final FileHandler fileHandler;
    private final ProductEventHandler eventHandler;

    @PostConstruct
    public void produceProducts() {
        eventHandler.addMessageHandler(productMessageHandler);
        eventHandler.addMessageHandler(logMessageHandler);
    }

    public void loadProducts() {
        log.info("importing products data from resources");
        try (var source = fileHandler.readFromFile("data/products.txt")) {
            source
                    .map(mapper::convertToDto)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(EventUtil::createEvent)
                    .forEach(eventHandler::sendEvent);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("finished writing to database.");
    }

    @Async
    @Override
    public void afterPropertiesSet() throws Exception {
        loadProducts();
    }

}
