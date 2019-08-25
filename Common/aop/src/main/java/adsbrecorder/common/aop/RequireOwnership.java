package adsbrecorder.common.aop;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface RequireOwnership {
//    @AliasFor("value")
//    boolean checkLatestCredentials() default true;
    boolean allowOverride() default false;
}
