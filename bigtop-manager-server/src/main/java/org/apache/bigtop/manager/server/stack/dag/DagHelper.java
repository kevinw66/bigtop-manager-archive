package org.apache.bigtop.manager.server.stack.dag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.EnumUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DagHelper {

    private static final String ROLE_COMMAND_SPLIT = "-";

    private static final Map<String, DAG<String, ComponentCommandWrapper, DagGraphEdge>> STACK_DAG_MAP = new HashMap<>();

    public static Map<String, DAG<String, ComponentCommandWrapper, DagGraphEdge>> getStackDagMap() {
        return Collections.unmodifiableMap(STACK_DAG_MAP);
    }

    /**
     * Initialize the DAG for each stack
     */
    public static void initializeDag() {
        for (Map.Entry<String, Map<String, List<String>>> mapEntry : StackUtils.getStackDependencyMap().entrySet()) {
            String fullStackName = mapEntry.getKey();
            DAG<String, ComponentCommandWrapper, DagGraphEdge> dag = new DAG<>();

            for (Map.Entry<String, List<String>> entry : mapEntry.getValue().entrySet()) {
                String blocked = entry.getKey();
                List<String> blockers = entry.getValue();

                String[] blockedTuple = blocked.split(ROLE_COMMAND_SPLIT);
                String blockedRole = blockedTuple[0];
                String blockedCommand = blockedTuple[1];
                ComponentCommandWrapper blockedRcp = new ComponentCommandWrapper(blockedRole, Command.valueOf(blockedCommand));
                dag.addNodeIfAbsent(blocked, blockedRcp);

                for (String blocker : blockers) {
                    String[] blockerTuple = blocker.split(ROLE_COMMAND_SPLIT);
                    String blockerRole = blockerTuple[0];
                    String blockerCommand = blockerTuple[1];

                    if (!EnumUtils.isValidEnum(Command.class, blockedCommand) || !EnumUtils.isValidEnum(Command.class, blockerCommand)) {
                        throw new ServerException("Unsupported command");
                    }

                    ComponentCommandWrapper blockerRcp = new ComponentCommandWrapper(blockerRole, Command.valueOf(blockerCommand));
                    dag.addNodeIfAbsent(blocker, blockerRcp);

                    // blocked --(requires)--> blocker
                    // eg. kafka install --(requires)--> zookeeper install
                    dag.addEdge(blocked, blocker, new DagGraphEdge(blocked, blocker), false);
                }
            }

            STACK_DAG_MAP.put(fullStackName, dag);
        }
    }
}
