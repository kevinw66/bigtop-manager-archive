package org.apache.bigtop.manager.stack.common.annotations;


import org.apache.bigtop.manager.stack.common.enums.HookType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HookGuard {

    HookType before() default HookType.NONE;

    HookType after() default HookType.NONE;

}
