package adsbrecorder.common.aop.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface RequestEntity {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    Class<?> idDataType() default Long.class;
}
