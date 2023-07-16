package org.apache.bigtop.manager.server;

import java.lang.reflect.Method;

import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ReflectionUtils;

public class MyRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register method for reflection
//        Method method = ReflectionUtils.findMethod(MyClass.class, "sayHello", String.class);
//        hints.reflection().registerMethod(method, ExecutableMode.INVOKE);

        // Register resources
//        hints.resources().registerPattern("my-resource.txt");

        // Register serialization
//        hints.serialization().registerType(HostInfo.class);

        // Register proxy
//        hints.proxies().registerJdkProxy(MyInterface.class);
    }

}

