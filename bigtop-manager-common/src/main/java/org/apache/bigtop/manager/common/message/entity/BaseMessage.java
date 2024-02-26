package org.apache.bigtop.manager.common.message.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BaseMessage {

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    private String messageId = UUID.randomUUID().toString();
}
