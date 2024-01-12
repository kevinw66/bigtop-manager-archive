package org.apache.bigtop.manager.stack.nop.v1_0_0.zookeeper;

import lombok.Getter;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.stack.spi.BaseParams;

@Getter
public class ZookeeperParams extends BaseParams {

    public ZookeeperParams(CommandPayload commandPayload) {
        super(commandPayload);
    }
}
