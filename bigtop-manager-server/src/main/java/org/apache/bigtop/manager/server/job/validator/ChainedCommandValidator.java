package org.apache.bigtop.manager.server.job.validator;

import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.job.CommandIdentifier;

import java.util.List;

public class ChainedCommandValidator {

    private static final List<CommandValidator> CHAIN = SpringContextHolder.getCommandValidators().values().stream().toList();

    public static void validate(ValidatorContext context, CommandIdentifier commandIdentifier) {
        for (CommandValidator validator : CHAIN) {
            if (validator.getCommandIdentifiers().contains(commandIdentifier)) {
                validator.validate(context);
            }
        }
    }
}
