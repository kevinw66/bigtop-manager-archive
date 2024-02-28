package org.apache.bigtop.manager.stack.nop.v1_0_0.zookeeper;

import lombok.Getter;
import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.stack.common.utils.BaseParams;

@Getter
public class ZookeeperParams extends BaseParams {

    public ZookeeperParams(CommandPayload commandPayload) {
        super(commandPayload);
    }
}
