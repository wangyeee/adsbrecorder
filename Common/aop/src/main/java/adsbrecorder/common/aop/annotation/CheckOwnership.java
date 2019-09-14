package adsbrecorder.common.aop.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import adsbrecorder.common.validator.OwnershipValidator;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface CheckOwnership {

    Class<? extends OwnershipValidator> validator() default OwnershipValidator.class;
}
