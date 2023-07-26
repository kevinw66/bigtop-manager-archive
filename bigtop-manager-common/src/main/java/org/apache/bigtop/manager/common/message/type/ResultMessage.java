package org.apache.bigtop.manager.common.message.type;

import lombok.*;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecific;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResultMessage extends BaseMessage {

    private Integer code;

    private String result;

    private String hostname;

}
