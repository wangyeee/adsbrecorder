package adsbrecorder.user.security;

import static java.util.Objects.requireNonNull;

import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.user.service.UserService;
import io.jsonwebtoken.Claims;

@Component
public class UserTokenAuthenticationFilter extends TokenAuthenticationFilter {

    private UserService userService;

    @Autowired
    public UserTokenAuthenticationFilter(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @Override
    protected Authentication tryAuthenticate(Claims claims) {
        return userService.findUserByName(claims.getSubject()).toAuthenticationToken();
    }

    @Override
    protected Key getSigningKey() {
        return userService.getSecretSigningKey();
    }
}
