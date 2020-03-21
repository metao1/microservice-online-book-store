package com.metao.product.retails.config;

import com.metao.product.retails.domain.AutoAwareItemEntity;
import com.metao.product.retails.model.BaseDTO;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        unmappedSourcePolicy = ReportingPolicy.WARN,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG,
        componentModel = "spring"
)
public interface BaseMappingConfig {

    AutoAwareItemEntity fromDto(BaseDTO baseDTO);

    BaseDTO toDto(AutoAwareItemEntity autoAwareItem);
}
