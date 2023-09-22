package org.apache.bigtop.manager.agent.runner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.bigtop.manager.common.message.type.HostCheckMessage;
import org.springframework.web.socket.WebSocketSession;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostCheckContext {

    private WebSocketSession session;

    private HostCheckMessage hostCheckMessage;

}
