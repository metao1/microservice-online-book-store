package com.metao.product.retails.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Table(value = "auto_item")
@AllArgsConstructor
@NoArgsConstructor
public class AutoAwareItemEntity {

    @PrimaryKey(value="id")
    private String id;

    private String createdBy;

    private String modifiedBy;

    private Instant createdAt;

    private Instant modifiedAt;

}
