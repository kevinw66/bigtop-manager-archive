package org.apache.bigtop.manager.server.job.validator;

import lombok.Data;
import org.apache.bigtop.manager.server.model.dto.CommandDTO;

import java.util.List;

@Data
public class ValidatorContext {

    private CommandDTO commandDTO;
}
