package org.apache.bigtop.manager.stack.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.stack.common.utils.Params;
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
        return scriptMap.get(commandMessage.getScriptId());
    }


    private Hook getHook(CommandMessage commandMessage) {
        return hookMap.get(commandMessage.getCommand().toLowerCase());
    }


    @Override
    public void execute(CommandMessage commandMessage) {
        Params.commandMessage = commandMessage;

        Script script = getScript(commandMessage);
        Hook hook = getHook(commandMessage);

        String command = commandMessage.getCommand();
        Hook hookAny = hookMap.get("any");

        hookAny.before();
        hook.before();

        try {
            Method method = script.getClass().getMethod(command.toLowerCase());
            method.invoke(script);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Execute command error, ", e);
            throw new RuntimeException(e);
        }

        hook.after();
        hookAny.after();

    }
}
