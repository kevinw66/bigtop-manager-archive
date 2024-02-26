package org.apache.bigtop.manager.common.message.entity.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.MessageType;


@EqualsAndHashCode(callSuper = true)
@Data
public class CommandRequestMessage extends BaseCommandMessage {

    private MessageType messageType;

    private String hostname;

    private String messagePayload;

}
