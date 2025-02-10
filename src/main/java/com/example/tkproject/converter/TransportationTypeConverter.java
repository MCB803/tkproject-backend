package com.example.tkproject.converter;

import com.example.tkproject.model.TransportationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransportationTypeConverter implements AttributeConverter<TransportationType, String> {

    @Override
    public String convertToDatabaseColumn(TransportationType attribute) {
        if (attribute == null) {
            return null;
        }
        // Save the enum in lower-case
        return attribute.name().toLowerCase();
    }

    @Override
    public TransportationType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            // Convert the lower-case string from the DB to the corresponding enum constant
            return TransportationType.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown transportation type: " + dbData, e);
        }
    }
}
