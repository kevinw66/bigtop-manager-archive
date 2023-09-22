package org.apache.bigtop.manager.agent.runner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.springframework.web.socket.WebSocketSession;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandContext {

    private WebSocketSession session;

    private CommandMessage commandMessage;

}
