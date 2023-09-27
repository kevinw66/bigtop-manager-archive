package org.apache.bigtop.manager.common.message.type;

import lombok.*;
import org.apache.bigtop.manager.common.enums.MessageType;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResultMessage extends BaseCommandMessage {

    private Integer code;

    private String result;

    private String hostname;

    private MessageType messageType;

}
