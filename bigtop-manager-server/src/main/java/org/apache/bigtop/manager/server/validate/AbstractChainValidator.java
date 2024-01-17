package org.apache.bigtop.manager.server.validate;

import org.apache.bigtop.manager.server.enums.ValidateType;

public abstract class AbstractChainValidator implements ChainValidator {

    ValidateType validateType;

    public void setValidateType() {
        this.validateType = ValidateType.DEFAULT;
    }

    public AbstractChainValidator() {
        this.setValidateType();
    }

    @Override
    public ValidateType getValidateType() {
        return this.validateType;
    }
}
