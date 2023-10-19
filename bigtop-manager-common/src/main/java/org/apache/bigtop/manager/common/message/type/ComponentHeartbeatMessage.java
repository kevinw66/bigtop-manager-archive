package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ComponentHeartbeatMessage extends BaseMessage {

    private Integer code;

    private String result;

    private String hostname;
}
