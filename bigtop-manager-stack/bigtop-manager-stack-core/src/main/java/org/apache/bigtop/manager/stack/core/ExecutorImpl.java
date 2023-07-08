package org.apache.bigtop.manager.stack.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.stack.common.AbstractParams;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;
import org.apache.bigtop.manager.stack.spi.SPIFactory;
import org.apache.bigtop.manager.stack.spi.Script;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class ExecutorImpl implements Executor {
    private static final Map<String, Script> scriptMap;
    private static final Map<String, Hook> hookMap;

    static {
        SPIFactory<Script> spiFactory = new SPIFactory<>(Script.class);
        scriptMap = spiFactory.getSPIMap();
        log.info("scriptMap: {}", scriptMap);

        SPIFactory<Hook> hookSPIFactory = new SPIFactory<>(Hook.class);
        hookMap = hookSPIFactory.getSPIMap();
    }


    private Script getScript(CommandMessage commandMessage) {
        return scriptMap.getOrDefault(commandMessage.getScriptId(), null);
    }


    private Hook getHook(CommandMessage commandMessage) {
        String command = commandMessage.getCommand().toLowerCase();
        return hookMap.getOrDefault(command, null);
    }


    @Override
    public Object execute(CommandMessage commandMessage) {
        AbstractParams.commandMessage = commandMessage;

        Script script = getScript(commandMessage);
        if (script == null) {
            throw new StackException("Cannot find Class {0}", commandMessage.getScriptId());
        }

        Hook hook = getHook(commandMessage);

        String command = commandMessage.getCommand();

        HookMethodUtils(hook, "before");

        Object object;
        try {
            Method method = script.getClass().getMethod(command.toLowerCase());
            object = method.invoke(script);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Execute command error, ", e);
            throw new StackException(e);
        }

        HookMethodUtils(hook, "after");
        return object;
    }

    public static void HookMethodUtils(Hook hook, String type) {
        if (hook == null) {
            return;
        }

        try {
            Method method = hook.getClass().getMethod(type);
            if (method.isAnnotationPresent(HookAnnotation.class)) {
                HookAnnotation annotation = method.getAnnotation(HookAnnotation.class);
                String before = annotation.before();
                Hook hookBefore = hookMap.getOrDefault(before, null);
                if (hookBefore != null) {
                    hookBefore.before();
                }

                method.invoke(hook);

                String after = annotation.after();
                Hook hookAfter = hookMap.getOrDefault(after, null);
                if (hookAfter != null) {
                    hookAfter.after();
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new StackException(e);
        }
    }
}
