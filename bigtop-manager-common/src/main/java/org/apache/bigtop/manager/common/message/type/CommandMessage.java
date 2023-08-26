package org.apache.bigtop.manager.common.message.type;

import lombok.*;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class CommandMessage extends BaseMessage {

    @NonNull
    private String service;

    @NonNull
    private String command;

    private String serviceUser;

    private String serviceGroup;

    @NonNull
    private String stack;

    @NonNull
    private String version;

    @NonNull
    private String root;

    private String component;

    @NonNull
    private String scriptId;

    @NonNull
    private String cacheDir;

    private List<OSSpecificInfo> osSpecifics;

    @NonNull
    private String hostname;

}
