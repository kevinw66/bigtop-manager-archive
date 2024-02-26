package org.apache.bigtop.manager.stack.nop.v1_0_0.kafka;

import lombok.Getter;
import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.stack.spi.BaseParams;

@Getter
public class KafkaParams extends BaseParams {

    public KafkaParams(CommandPayload commandPayload) {
        super(commandPayload);
    }
}
