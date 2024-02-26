package org.apache.bigtop.manager.common.message.entity.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.message.entity.BaseMessage;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class BaseCommandMessage extends BaseMessage {

    private Long jobId;

    private Long stageId;

    private Long taskId;
}
