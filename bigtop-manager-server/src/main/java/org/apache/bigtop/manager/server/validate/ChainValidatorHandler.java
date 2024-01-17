package org.apache.bigtop.manager.server.validate;

import org.apache.bigtop.manager.server.enums.ValidateType;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;

import java.util.Map;

public class ChainValidatorHandler {

    public static void handleRequest(ChainContext context, ValidateType validateType) {
        Map<String, ChainValidator> validatorMap = SpringContextHolder.getValidator();

        for (ChainValidator validator : validatorMap.values()) {
            if (validator.getValidateType() == validateType || validator.getValidateType() == ValidateType.DEFAULT) {
                validator.vaildate(context);
            }
        }
    }
}
