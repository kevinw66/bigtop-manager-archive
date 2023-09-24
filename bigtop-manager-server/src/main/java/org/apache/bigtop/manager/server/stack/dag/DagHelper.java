package org.apache.bigtop.manager.server.stack.dag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.commons.lang3.EnumUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DagHelper {

    private static final String ROLE_COMMAND_SPLIT = "-";

    private static final Map<String, DAG<ComponentCommandWrapper, ComponentCommandWrapper, DagGraphEdge>> STACK_DAG_MAP = new HashMap<>();

    public static Map<String, DAG<ComponentCommandWrapper, ComponentCommandWrapper, DagGraphEdge>> getStackDagMap() {
        return Collections.unmodifiableMap(STACK_DAG_MAP);
    }

    /**
     * Initialize the DAG for each stack
     * @param stackDependencyMap {@code Map<BIGTOP-3.2.0=Map<blockedRole, Set<blockerRole>>>}
     */
    public static void dagInitialized(Map<String, Map<String, Set<String>>> stackDependencyMap) {

        for (Map.Entry<String, Map<String, Set<String>>> mapEntry : stackDependencyMap.entrySet()) {

            String fullStackName = mapEntry.getKey();
            DAG<ComponentCommandWrapper, ComponentCommandWrapper, DagGraphEdge> dag = new DAG<>();

            for (Map.Entry<String, Set<String>> entry : mapEntry.getValue().entrySet()) {
                String key = entry.getKey();
                Set<String> blockers = entry.getValue();

                String[] blockedTuple = key.split(ROLE_COMMAND_SPLIT);
                String blockedRole = blockedTuple[0];
                String blockedCommand = blockedTuple[1];

                for (String blocker : blockers) {
                    String[] blockerTuple = blocker.split(ROLE_COMMAND_SPLIT);
                    String blockerRole = blockerTuple[0];
                    String blockerCommand = blockerTuple[1];

                    if (!EnumUtils.isValidEnum(Command.class, blockedCommand) || !EnumUtils.isValidEnum(Command.class, blockerCommand)) {
                        throw new RuntimeException("Unsupported command");
                    }
                    ComponentCommandWrapper blockedRcp = new ComponentCommandWrapper(blockedRole, Command.valueOf(blockedCommand));
                    ComponentCommandWrapper blockerRcp = new ComponentCommandWrapper(blockerRole, Command.valueOf(blockerCommand));

                    DagGraphEdge roleCommandEdge = new DagGraphEdge(blockerRcp, blockedRcp);
                    dag.addEdge(blockerRcp, blockedRcp, roleCommandEdge, true);
                }

                STACK_DAG_MAP.put(fullStackName, dag);
            }
        }


    }

}
