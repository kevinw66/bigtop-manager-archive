package org.apache.bigtop.manager.common.message.type;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.pojo.stack.OSSpecific;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class CommandMessage extends BaseMessage {

    private String service;

    private String command;

    private String serviceUser;

    private String serviceGroup;

    private String stack;

    private String version;

    private String root;

    private String component;

    private String scriptId;

    private String cacheDir;

    private List<OSSpecific> osSpecifics;

    private String hostname;

}
