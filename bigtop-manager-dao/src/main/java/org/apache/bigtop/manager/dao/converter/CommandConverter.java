package org.apache.bigtop.manager.dao.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.utils.CaseUtils;

@Converter(autoApply = true)
public class CommandConverter implements AttributeConverter<Command, String> {

    @Override
    public String convertToDatabaseColumn(Command attribute) {
        return attribute == null ? null : attribute.toCamelCase();
    }

    @Override
    public Command convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Command.fromString(CaseUtils.toHyphenCase(dbData));
    }
}
