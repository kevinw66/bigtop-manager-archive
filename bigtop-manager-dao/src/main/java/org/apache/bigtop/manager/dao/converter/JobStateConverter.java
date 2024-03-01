package org.apache.bigtop.manager.dao.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.bigtop.manager.common.enums.JobState;
import org.apache.bigtop.manager.common.utils.CaseUtils;

@Converter(autoApply = true)
public class JobStateConverter implements AttributeConverter<JobState, String> {

    @Override
    public String convertToDatabaseColumn(JobState attribute) {
        return attribute == null ? null : attribute.toCamelCase();
    }

    @Override
    public JobState convertToEntityAttribute(String dbData) {
        return dbData == null ? null : JobState.fromString(CaseUtils.toHyphenCase(dbData));
    }
}
