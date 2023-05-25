package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class CommandMessage extends BaseMessage {

    private String service;

    private String beforeCommand;

    private String command;

    private String afterCommand;

    private String stack;

    private String version;

    private String component;

    private String scriptId;

    private String cacheDir;
}
