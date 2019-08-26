package adsbrecorder.common.aop.conf;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import adsbrecorder.common.aop.LoginUserArgumentResolver;
import adsbrecorder.user.service.UserService;

@Configuration
public class LoginUserArgumentResolverConfiguration implements WebMvcConfigurer {

    private UserService userService;

    @Autowired
    public LoginUserArgumentResolverConfiguration(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new LoginUserArgumentResolver(userService));
    }
}
