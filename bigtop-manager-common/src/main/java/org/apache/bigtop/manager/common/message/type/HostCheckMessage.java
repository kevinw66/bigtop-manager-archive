package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class HostCheckMessage extends BaseMessage {

    private String hostname;

    private HostCheckType[] hostCheckTypes;

}
