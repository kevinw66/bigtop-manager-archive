package org.apache.bigtop.manager.server.job.validator;

import org.apache.bigtop.manager.server.job.CommandIdentifier;

import java.util.List;

public interface CommandValidator {

    List<CommandIdentifier> getCommandIdentifiers();

    void validate(ValidatorContext context);
}
