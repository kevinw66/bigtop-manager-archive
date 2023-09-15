package org.apache.bigtop.manager.stack.core.annotations;


import org.apache.bigtop.manager.stack.common.enums.HookType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HookAnnotation {

    HookType before() default HookType.NONE;

    HookType after() default HookType.NONE;

}
