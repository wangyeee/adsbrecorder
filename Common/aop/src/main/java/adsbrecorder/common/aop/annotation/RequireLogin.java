package adsbrecorder.common.aop.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Retention(RUNTIME)
@Target(METHOD)
public @interface RequireLogin {
    @AliasFor("value")
    boolean checkLatestCredentials() default true;
}
