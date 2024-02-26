package org.apache.bigtop.manager.common.message.entity.command;

import lombok.*;
import org.apache.bigtop.manager.common.enums.MessageType;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommandResponseMessage extends BaseCommandMessage {

    private Integer code;

    private String result;

    private String hostname;

    private MessageType messageType;

}
