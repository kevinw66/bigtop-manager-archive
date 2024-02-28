package org.apache.bigtop.manager.common.message.entity.command;

import lombok.*;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.entity.BaseResponseMessage;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommandResponseMessage extends BaseResponseMessage {

    private Integer code;

    private String result;

    private String hostname;

    private MessageType messageType;

    private Long jobId;

    private Long stageId;

    private Long taskId;
}
