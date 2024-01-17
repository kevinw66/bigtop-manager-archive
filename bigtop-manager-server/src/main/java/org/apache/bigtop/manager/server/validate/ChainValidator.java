package org.apache.bigtop.manager.server.validate;

import org.apache.bigtop.manager.server.enums.ValidateType;

public interface ChainValidator {

    void vaildate(ChainContext context);

    ValidateType getValidateType();

}
