package org.apache.bigtop.manager.dao.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.bigtop.manager.common.enums.MaintainState;
import org.apache.bigtop.manager.common.utils.CaseUtils;

@Converter(autoApply = true)
public class MaintainStateConverter implements AttributeConverter<MaintainState, String> {

    @Override
    public String convertToDatabaseColumn(MaintainState attribute) {
        return attribute == null ? null : attribute.toCamelCase();
    }

    @Override
    public MaintainState convertToEntityAttribute(String dbData) {
        return dbData == null ? null : MaintainState.fromString(CaseUtils.toHyphenCase(dbData));
    }
}
