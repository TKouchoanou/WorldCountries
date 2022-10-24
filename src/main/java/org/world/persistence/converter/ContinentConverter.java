package org.world.persistence.converter;

import org.world.references.Continent;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;
@Converter(autoApply = true)
@SuppressWarnings("unused")
public class ContinentConverter implements AttributeConverter<Continent,String> {
    @Override
    public String convertToDatabaseColumn(Continent continent) {
        return Optional.ofNullable(continent).map(Continent::getNom).orElse(null);
    }

    @Override
    public Continent convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData).map(Continent::of).orElse(null);
    }
}
