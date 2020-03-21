package com.metao.product.retails.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@SuperBuilder
public class BaseEntity {

    @PrimaryKey("asin")
    private String id;

    private String createdBy;

    private String modifiedBy;

    private Instant createdAt;

    private Instant modifiedAt;

}
