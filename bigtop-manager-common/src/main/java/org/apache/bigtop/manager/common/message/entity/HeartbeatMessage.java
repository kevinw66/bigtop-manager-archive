package org.apache.bigtop.manager.common.message.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.message.entity.pojo.HostInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class HeartbeatMessage extends BaseMessage {

    private HostInfo hostInfo;
}
