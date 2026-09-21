package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Converter
public class LocalDateTimeToLocalDateConverter implements AttributeConverter<LocalDateTime, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute == null ? null : attribute.toLocalDate();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(LocalDate databaseValue) {
        return databaseValue == null ? null : databaseValue.atStartOfDay();
    }
}