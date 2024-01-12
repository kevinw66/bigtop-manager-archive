package org.apache.bigtop.manager.stack.nop.v1_0_0.kafka;

import lombok.Getter;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.stack.common.utils.LocalSettings;
import org.apache.bigtop.manager.stack.spi.BaseParams;

import java.util.Map;

@Getter
public class KafkaParams extends BaseParams {

    public KafkaParams(CommandPayload commandPayload) {
        super(commandPayload);
    }
}
