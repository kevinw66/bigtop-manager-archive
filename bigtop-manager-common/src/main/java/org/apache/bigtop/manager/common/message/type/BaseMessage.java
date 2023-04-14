package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class BaseMessage {

    private Timestamp timestamp;
}
