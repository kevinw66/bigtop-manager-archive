package org.apache.bigtop.manager.stack.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.common.utils.Params;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.Hook;
import org.apache.bigtop.manager.stack.spi.SPIFactory;
import org.apache.bigtop.manager.stack.spi.Script;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class ExecuteImpl implements Execute {
    private static final Map<String, Script> scriptMap;
    private static final Map<String, Hook> hookMap;

    static {
        SPIFactory<Script> spiFactory = new SPIFactory<>(Script.class);
        scriptMap = spiFactory.getSPIMap();
        log.info("scriptMap: {}", scriptMap);
        System.out.println(scriptMap);

        SPIFactory<Hook> hookSPIFactory = new SPIFactory<>(Hook.class);
        hookMap = hookSPIFactory.getSPIMap();
    }


    private Script getScript(CommandMessage commandMessage) {
        log.info("scriptMap: {}", scriptMap);
        return scriptMap.getOrDefault(commandMessage.getScriptId(), null);
    }


    private Hook getHook(CommandMessage commandMessage) {
        String command = commandMessage.getCommand().toLowerCase();
        return hookMap.getOrDefault(command, null);
    }


    @Override
    public void execute(CommandMessage commandMessage) {
        Params.commandMessage = commandMessage;

        Script script = getScript(commandMessage);
        if (script == null) {
            throw new StackException("Cannot find Class {0}", commandMessage.getScriptId());
        }

        Hook hook = getHook(commandMessage);

        String command = commandMessage.getCommand();
        if (hook != null) {
            HookMethodUtils(hook.getClass(), "before");
            hook.before();
        }

        try {
            Method method = script.getClass().getMethod(command.toLowerCase());
            method.invoke(script);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Execute command error, ", e);
            throw new StackException(e);
        }

        if (hook != null) {
            hook.after();
            HookMethodUtils(hook.getClass(), "after");
        }

    }

    public static <T> void HookMethodUtils(Class<T> clazz, String type) {
        try {
            Method method = clazz.getMethod(type);
            if (method.isAnnotationPresent(HookAnnotation.class)) {
                HookAnnotation annotation = method.getAnnotation(HookAnnotation.class);
                String before = annotation.before();
                Hook hookBefore = hookMap.getOrDefault(before, null);
                if (hookBefore != null) {
                    hookBefore.before();
                }
                String after = annotation.after();
                Hook hookAfter = hookMap.getOrDefault(after, null);
                if (hookAfter != null) {
                    hookAfter.after();
                }
            }
        } catch (NoSuchMethodException e) {
            throw new StackException(e);
        }
    }
}
