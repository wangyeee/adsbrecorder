package adsbrecorder.common.aop;

import static java.util.Objects.requireNonNull;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import adsbrecorder.security.exception.AuthorizationExpiredException;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@Aspect
@Component
public class RequireLoginAspect {

    private UserService userService;

    @Autowired
    public RequireLoginAspect(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @Before("@annotation(adsbrecorder.common.aop.RequireLogin) && execution(public * *(..))")
    public void requireLoginCheck(final JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequireLogin annotation = signature.getMethod().getAnnotation(RequireLogin.class);
        if (annotation.checkLatestCredentials()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.loginHash(String.valueOf(auth.getPrincipal()),
                    String.valueOf(auth.getCredentials()));
            if (user == null) {
                throw new AuthorizationExpiredException();
            }
        }
    }
}
