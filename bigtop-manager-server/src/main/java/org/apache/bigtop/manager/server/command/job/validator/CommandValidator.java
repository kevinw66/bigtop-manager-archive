package org.apache.bigtop.manager.server.command.job.validator;

import org.apache.bigtop.manager.server.command.CommandIdentifier;

import java.util.List;

public interface CommandValidator {

    List<CommandIdentifier> getCommandIdentifiers();

    void validate(ValidatorContext context);
}
