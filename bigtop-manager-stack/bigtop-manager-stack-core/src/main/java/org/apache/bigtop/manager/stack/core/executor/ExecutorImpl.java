package org.apache.bigtop.manager.stack.core.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.type.CommandPayload;
import org.apache.bigtop.manager.common.message.type.pojo.CustomCommandInfo;
import org.apache.bigtop.manager.stack.common.enums.HookAroundType;
import org.apache.bigtop.manager.stack.common.enums.HookType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;
import org.apache.bigtop.manager.stack.spi.SPIFactory;
import org.apache.bigtop.manager.stack.spi.Script;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExecutorImpl implements Executor {
    private final Map<String, Script> scriptMap;
    private final Map<String, Hook> hookMap;

    public ExecutorImpl() {
        SPIFactory<Script> spiFactory = new SPIFactory<>(Script.class);
        scriptMap = spiFactory.getSPIMap();
        log.info("scriptMap initialized: {}", scriptMap);

        SPIFactory<Hook> hookSPIFactory = new SPIFactory<>(Hook.class);
        hookMap = hookSPIFactory.getSPIMap();
    }

    private Script getCommandScript(String scriptId) {
        Script script = scriptMap.get(scriptId);
        if (script == null) {
            throw new StackException("Cannot find Script Class {0}", scriptId);
        }
        return script;
    }

    private Script getCustomScript(String customCommand, List<CustomCommandInfo> customCommands) {
        Script script = null;
        String scriptId = null;
        for (CustomCommandInfo customCommandInfo : customCommands) {
            if (customCommandInfo.getName().equals(customCommand)) {
                scriptId = customCommandInfo.getCommandScript().getScriptId();
                script = getCommandScript(scriptId);
            }
        }
        if (script == null) {
            throw new StackException("Cannot find Script Class {0}", scriptId);
        }
        return script;
    }

    private Hook getHook(CommandPayload commandMessage) {
        return hookMap.get(commandMessage.getCommand().name());
    }

    @Override
    public Object execute(CommandPayload commandMessage) {
        Script script = getCommandScript(commandMessage.getCommandScript().getScriptId());

        Hook hook = getHook(commandMessage);
        String command = commandMessage.getCommand().name();

        hookAspect(hook, HookAroundType.BEFORE.getType());

        Object result;
        try {
            Method method;
            if (command.equals(Command.CUSTOM_COMMAND.name())) {
                String customCommand = commandMessage.getCustomCommand();
                script = getCustomScript(customCommand, commandMessage.getCustomCommands());
                method = script.getClass().getMethod(customCommand.toLowerCase(), CommandPayload.class);
            } else {
                method = script.getClass().getMethod(command.toLowerCase(), CommandPayload.class);
            }
            log.info("start execute [{}] : [{}]", script.getName(), method.getName());
            result = method.invoke(script, commandMessage);
            log.info("execute [{}] : [{}] complete, result: [{}]", script.getName(), method.getName(), result);
        } catch (Exception e) {
            log.info("execute [{}] error", script.getName());
            throw new StackException(e);
        }

        hookAspect(hook, HookAroundType.AFTER.getType());
        return result;
    }

    private void hookAspect(Hook hook, String type) {
        if (hook == null) {
            return;
        }

        try {
            Method method = hook.getClass().getMethod(type);
            if (method.isAnnotationPresent(HookAnnotation.class)) {
                HookAnnotation annotation = method.getAnnotation(HookAnnotation.class);
                HookType before = annotation.before();
                Hook hookBefore = hookMap.get(before.name());
                if (hookBefore != null) {
                    log.info("execute hook before: {}", hookBefore.getName());
                    hookBefore.before();
                }
                log.info("execute hook: {}", hook.getName());
                method.invoke(hook);

                HookType after = annotation.after();
                Hook hookAfter = hookMap.get(after.name());
                if (hookAfter != null) {
                    log.info("execute hook after: {}", hookAfter.getName());
                    hookAfter.after();
                }
            }
        } catch (Exception e) {
            throw new StackException(e);
        }
    }
}
