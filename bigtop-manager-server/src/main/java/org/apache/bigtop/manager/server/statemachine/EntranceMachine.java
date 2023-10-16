package org.apache.bigtop.manager.server.statemachine;


import lombok.Data;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.heartbeat.CommandState;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
@Component
public class EntranceMachine {

    List<EntranceMachineTransaction> entranceMachineTransactionList = Arrays.asList(
            EntranceMachineTransaction.builder()
                    .currentState(CommandState.UNINSTALLED)
                    .action(Command.INSTALL)
                    .nextState(CommandState.INSTALLED)
                    .event(new StartEvent())
                    .build(),
            EntranceMachineTransaction.builder()
                    .currentState(CommandState.UNINSTALLED)
                    .action(Command.START)
                    .nextState(CommandState.STARTED)
                    .event(new StartEvent())
                    .build(),
            EntranceMachineTransaction.builder()
                    .currentState(CommandState.STOPPED)
                    .action(Command.START)
                    .nextState(CommandState.STARTED)
                    .event(new StartEvent())
                    .build(),
            EntranceMachineTransaction.builder()
                    .currentState(CommandState.STARTED)
                    .action(Command.STOP)
                    .nextState(CommandState.STOPPED)
                    .event(new StartEvent())
                    .build(),
            EntranceMachineTransaction.builder()
                    .currentState(CommandState.STOPPED)
                    .action(Command.UNINSTALL)
                    .nextState(CommandState.UNINSTALLED)
                    .event(new StartEvent())
                    .build()
    );

    private CommandState state;

    public String execute(Command action) {
        Optional<EntranceMachineTransaction> transactionOptional = entranceMachineTransactionList
                .stream()
                .filter(transaction ->
                        transaction.getAction().equals(action) && transaction.getCurrentState().equals(state))
                .findFirst();

        if (!transactionOptional.isPresent()) {
            throw new ServerException("InvalidActionException");
        }

        EntranceMachineTransaction transaction = transactionOptional.get();
        setState(transaction.getNextState());
        return transaction.getEvent().execute();
    }
}
