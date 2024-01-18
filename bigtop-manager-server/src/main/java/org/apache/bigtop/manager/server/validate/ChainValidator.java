package org.apache.bigtop.manager.server.validate;

import org.apache.bigtop.manager.server.enums.ValidateType;

public interface ChainValidator {

    ValidateType getValidateType();

    void validate(ValidatorContext context);
}
