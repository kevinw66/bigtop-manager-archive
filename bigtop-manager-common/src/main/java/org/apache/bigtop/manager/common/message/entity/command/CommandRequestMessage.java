package org.apache.bigtop.manager.common.message.entity.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.bigtop.manager.common.message.entity.BaseRequestMessage;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class CommandRequestMessage extends BaseRequestMessage {

    private CommandMessageType commandMessageType;

    private String hostname;

    private String messagePayload;

    private Long jobId;

    private Long stageId;

    private Long taskId;
}
