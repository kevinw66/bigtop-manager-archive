package org.apache.bigtop.manager.stack.core.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.entity.payload.CommandPayload;
import org.apache.bigtop.manager.common.message.entity.pojo.CustomCommandInfo;
import org.apache.bigtop.manager.common.utils.shell.DefaultShellResult;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.enums.HookAroundType;
import org.apache.bigtop.manager.stack.common.enums.HookType;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.bigtop.manager.stack.core.annotations.HookAnnotation;
import org.apache.bigtop.manager.stack.spi.BaseParams;
import org.apache.bigtop.manager.stack.spi.Hook;
import org.apache.bigtop.manager.stack.spi.SPIFactory;
import org.apache.bigtop.manager.stack.spi.Script;
import org.apache.commons.text.CaseUtils;

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

    private Hook getHook(CommandPayload commandPayload) {
        return hookMap.get(commandPayload.getCommand().name());
    }

    @Override
    public ShellResult execute(CommandPayload commandPayload) {
        try {
            Script script = getCommandScript(commandPayload.getCommandScript().getScriptId());
            String command = commandPayload.getCommand().name();

            Hook hook = getHook(commandPayload);
            hookAspect(hook, HookAroundType.BEFORE.getType());

            String paramsPackageName = script.getClass().getPackageName() + "." + CaseUtils.toCamelCase(commandPayload.getServiceName(), true) + "Params";
            Class<?> paramsClass = Class.forName(paramsPackageName);
            BaseParams baseParams = (BaseParams) paramsClass.getDeclaredConstructor(CommandPayload.class).newInstance(commandPayload);

            Method method;
            if (command.equals(Command.CUSTOM_COMMAND.name())) {
                String customCommand = commandPayload.getCustomCommand();
                script = getCustomScript(customCommand, commandPayload.getCustomCommands());
                method = script.getClass().getMethod(customCommand.toLowerCase(), BaseParams.class);
            } else {
                method = script.getClass().getMethod(command.toLowerCase(), BaseParams.class);
            }
            if (!command.equals(Command.STATUS.name())) {
                log.info("start execute [{}] : [{}]", script.getName(), method.getName());
            }
            ShellResult result = (ShellResult) method.invoke(script, baseParams);
            if (!command.equals(Command.STATUS.name())) {
                log.info("execute [{}] : [{}] complete, result: [{}]", script.getName(), method.getName(), result);
            }
            hookAspect(hook, HookAroundType.AFTER.getType());
            return result;
        } catch (Exception e) {
            log.info("Execute for commandPayload [{}] Error!!!", commandPayload, e);
            return DefaultShellResult.FAIL;
        }

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
