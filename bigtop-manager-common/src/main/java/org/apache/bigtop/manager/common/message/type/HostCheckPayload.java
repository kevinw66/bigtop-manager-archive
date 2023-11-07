package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.message.type.pojo.HostCheckType;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class HostCheckPayload {

    private String hostname;

    private HostCheckType[] hostCheckTypes;

}
