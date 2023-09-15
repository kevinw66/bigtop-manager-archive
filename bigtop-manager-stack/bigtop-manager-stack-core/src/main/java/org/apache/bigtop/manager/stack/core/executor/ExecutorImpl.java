package org.apache.bigtop.manager.stack.core.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.stack.common.enums.HookAroundType;
import org.apache.bigtop.manager.stack.common.enums.HookType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;
import org.apache.bigtop.manager.stack.spi.SPIFactory;
import org.apache.bigtop.manager.stack.spi.Script;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Method;
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

    private Script getScript(CommandMessage commandMessage) {
        return scriptMap.get(commandMessage.getScriptId());
    }

    private Hook getHook(CommandMessage commandMessage) {
        return hookMap.get(commandMessage.getCommand());
    }

    @Override
    public Object execute(CommandMessage commandMessage) {
        Script script = getScript(commandMessage);
        if (script == null) {
            throw new StackException("Cannot find Class {0}", commandMessage.getScriptId());
        }

        Hook hook = getHook(commandMessage);
        String command = commandMessage.getCommand();

        hookAspect(hook, HookAroundType.BEFORE.getType());

        Object result;
        try {
            Method method = script.getClass().getMethod(command.toLowerCase(), CommandMessage.class);
            log.info("method: {}", method);
            result = method.invoke(script, commandMessage);
        } catch (Exception e) {
            log.error("Execute command error, ", e);
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
                    hookBefore.before();
                }

                method.invoke(hook);

                HookType after = annotation.after();
                Hook hookAfter = hookMap.get(after.name());
                if (hookAfter != null) {
                    hookAfter.after();
                }
            }
        } catch (Exception e) {
            throw new StackException(e);
        }
    }
}
