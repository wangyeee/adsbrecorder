package adsbrecorder.reporting.security;

import static java.util.Objects.requireNonNull;

import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import adsbrecorder.common.auth.ListOfAudiences;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;
import io.jsonwebtoken.Claims;

@Component
public class ReportTokenAuthenticationFilter extends TokenAuthenticationFilter implements ListOfAudiences {

    private UserService userService;

    @Autowired
    public ReportTokenAuthenticationFilter(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @Override
    protected Authentication tryAuthenticate(Claims claims) {
        if (USER.equals(claims.getAudience())) {
            User user = userService.findUserByName(claims.getSubject());
            return user.toAuthenticationToken();
        }
        return new UsernamePasswordAuthenticationToken(null, null);
    }

    @Override
    protected Key getSigningKey() {
        return userService.getSecretSigningKey();
    }
}
