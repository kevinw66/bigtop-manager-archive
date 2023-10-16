package org.apache.bigtop.manager.server.statemachine;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.heartbeat.CommandState;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntranceMachineTransaction {

    private CommandState currentState;

    private Command action;

    private CommandState nextState;

    private Event event;
}