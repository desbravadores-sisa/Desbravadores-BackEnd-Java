package school.sptech.APIDesbravadores.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Converter
public class LocalDateToLocalDateTimeConverter implements AttributeConverter<LocalDate, LocalDateTime> {

    @Override
    public LocalDateTime convertToDatabaseColumn(LocalDate attribute) {
        return attribute == null ? null : attribute.atStartOfDay();
    }

    @Override
    public LocalDate convertToEntityAttribute(LocalDateTime databaseValue) {
        return databaseValue == null ? null : databaseValue.toLocalDate();
    }
}