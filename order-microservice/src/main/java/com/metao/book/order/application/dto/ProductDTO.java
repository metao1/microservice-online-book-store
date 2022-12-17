package com.metao.book.order.application.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import org.apache.avro.reflect.AvroDoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AvroDoc("""
    The product object
    """
)
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {

    @AvroDoc("identifier of the product")
    @Pattern(regexp = "^\\d{10}", message = "asin format is wrong")
    private String asin;
    
}