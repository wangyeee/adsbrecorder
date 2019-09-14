package adsbrecorder.common.aop;

import static java.util.Objects.requireNonNull;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import adsbrecorder.common.aop.annotation.LoginUser;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserService userService;

    public LoginUserArgumentResolver(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loginHash(String.valueOf(auth.getPrincipal()),
                String.valueOf(auth.getCredentials()));
        return user;
    }
}
