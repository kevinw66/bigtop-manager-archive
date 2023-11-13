package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.common.enums.MessageType;


@EqualsAndHashCode(callSuper = true)
@Data
public class RequestMessage extends BaseCommandMessage {

    private MessageType messageType;

    private String hostname;

    private String messagePayload;

}
