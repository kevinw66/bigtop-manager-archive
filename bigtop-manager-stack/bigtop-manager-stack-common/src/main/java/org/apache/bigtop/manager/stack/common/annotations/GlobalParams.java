package org.apache.bigtop.manager.stack.common.annotations;


import java.lang.annotation.*;

/**
 * The method return value that requires the current annotation tag is injected into the global parameter variable.
 * The return parameter must be {@link java.util.Map(java.lang.String, java.lang.Object)}.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalParams {

}
