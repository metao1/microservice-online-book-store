package com.metao.product.retails.exception;

import com.metao.product.retails.model.ProductApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ProductExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({ProductNotFoundException.class})
    public ResponseEntity<ProductApiErrorDTO> productNotFoundException(String productId) {
        return new ResponseEntity<>(ProductApiErrorDTO
                .builder()
                .message(String.format("Product %s not found", productId))
                .build(), HttpStatus.NOT_FOUND);
    }
}
