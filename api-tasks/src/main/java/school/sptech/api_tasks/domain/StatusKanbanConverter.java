package school.sptech.api_tasks.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusKanbanConverter implements AttributeConverter<StatusKanban, String> {

    @Override
    public String convertToDatabaseColumn(StatusKanban attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDescricao();
    }

    @Override
    public StatusKanban convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return StatusKanban.fromString(dbData);
    }
}
