package com.metao.book.shared.domain.geo.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.metao.book.shared.domain.geo.CityName;

/**
 * JPA attribute converter for {@link CityName}.
 */
@Converter(autoApply = true)
public class CityNameConverter implements AttributeConverter<CityName, String> {

    @Override
    public String convertToDatabaseColumn(CityName attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public CityName convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new CityName(dbData);
    }
}
