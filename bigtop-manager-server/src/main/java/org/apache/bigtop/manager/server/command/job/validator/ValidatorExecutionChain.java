package org.apache.bigtop.manager.server.command.job.validator;

import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.command.CommandIdentifier;

import java.util.List;

public class ValidatorExecutionChain {

    private static final List<CommandValidator> CHAIN = SpringContextHolder.getCommandValidators().values().stream().toList();

    public static void execute(ValidatorContext context, CommandIdentifier commandIdentifier) {
        for (CommandValidator validator : CHAIN) {
            if (validator.getCommandIdentifiers().contains(commandIdentifier)) {
                validator.validate(context);
            }
        }
    }
}
