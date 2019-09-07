package adsbrecorder.common.aop;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import adsbrecorder.common.aop.annotation.AnnotationUtils;
import adsbrecorder.common.aop.annotation.CheckOwnership;
import adsbrecorder.common.validator.OwnershipValidator;
import adsbrecorder.security.exception.AuthorizationExpiredException;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@Aspect
@Component
public class RequireOwnershipAspect implements AnnotationUtils {

    private UserService userService;
    private ApplicationContext applicationContext;
    private volatile Map<Class<? extends OwnershipValidator>, OwnershipValidator> cachedOwnershipValidators;

    @Autowired
    public RequireOwnershipAspect(UserService userService, ApplicationContext applicationContext) {
        this.userService = requireNonNull(userService);
        this.applicationContext = requireNonNull(applicationContext);
    }

    @PostConstruct
    public void cacheValidators() {
        if (this.cachedOwnershipValidators != null) return;
        this.cachedOwnershipValidators = new ConcurrentHashMap<Class<? extends OwnershipValidator>, OwnershipValidator>();
        String[] beanNames = this.applicationContext.getBeanDefinitionNames();
        Arrays.stream(beanNames).forEach(name -> {
            Object bean = this.applicationContext.getBean(name);
            if (bean instanceof OwnershipValidator) {
                final OwnershipValidator bean0 = (OwnershipValidator) bean;
                cachedOwnershipValidators.put(bean0.getClass(), bean0);
            }
        });
    }

    @Before("@annotation(adsbrecorder.common.aop.RequireOwnership) && execution(public * *(..))")
    public void requireOwnershipCheck(final JoinPoint joinPoint) throws Throwable {
        Signature s = joinPoint.getSignature();
        if (s instanceof MethodSignature) {
            final MethodSignature signature = (MethodSignature) s;
            Object[] parameterValues = joinPoint.getArgs();
            Object id = null;
            OwnershipValidator validator = null;
            int index[] = new int[1];
            CheckOwnership paramAnnotation = searchFirst(signature, CheckOwnership.class, index);
            if (paramAnnotation != null) {
                id = parameterValues[index[0]];
                validator = this.cachedOwnershipValidators.get(paramAnnotation.validator());
            }
            if (id == null) {
                // TODO log
                System.err.println("[WARNING] no id to validate: " + signature.toShortString());
            } else if (validator == null) {
                System.err.println("[WARNING] no validator available: " + signature.toShortString());
            } else {
                User currentUser = fromSecurityContext();
                // perform the acutal check
                boolean valid = validator.check(currentUser, id);
                if (!valid) {
                    throw new RuntimeException(String.format("Ownership check fail: User=%s, obj=%s",
                            currentUser.getUserId().toString(), String.valueOf(id)));
                }
            }
        }
    }

    private User fromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loginHash(String.valueOf(auth.getPrincipal()),
                String.valueOf(auth.getCredentials()));
        if (user == null) {
            throw new AuthorizationExpiredException();
        }
        return user;
    }
}
