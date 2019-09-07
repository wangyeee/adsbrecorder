package adsbrecorder.common.aop.conf;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import adsbrecorder.common.aop.PathEntityArgumentResolver;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.RoleService;
import adsbrecorder.user.service.UserService;

@Configuration
public class PathEntityArgumentResolverConfiguration implements WebMvcConfigurer {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        Map<Class<?>, Function<? super Number, ?>> map = Map.of(
            User.class, (id) -> this.userService.findUserById((Long) id),
            Role.class, (id) -> this.roleService.findRoleById((Long) id)
        );
        argumentResolvers.add(new PathEntityArgumentResolver(map));
    }
}
