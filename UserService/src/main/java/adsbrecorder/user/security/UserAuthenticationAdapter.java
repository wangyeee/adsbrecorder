package adsbrecorder.user.security;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

import adsbrecorder.common.auth.ListOfAuthorities;
import adsbrecorder.common.auth.TokenAuthenticationAdapter;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.user.UserServiceMappings;

@EnableWebSecurity
@Component
public class UserAuthenticationAdapter extends TokenAuthenticationAdapter implements UserServiceMappings, ListOfAuthorities {

    private UserTokenAuthenticationFilter userTokenAuthenticationFilter;

    @Autowired
    public UserAuthenticationAdapter(UserTokenAuthenticationFilter userTokenAuthenticationFilter) {
        this.userTokenAuthenticationFilter = requireNonNull(userTokenAuthenticationFilter);
    }

    @Override
    protected TokenAuthenticationFilter getTokenAuthenticationFilter() {
        return this.userTokenAuthenticationFilter;
    }

    @Override
    protected Collection<String> permitAllURLs() {
        return List.of(USER_NEW, USER_LOGIN, USERNAME_CHECK, USER_LOGOUT);
    }

    @Override
    protected Map<String, String> hasAnyAuthorityURLs() {
        return Map.of(
            ROLE_ASSIGNED_USERS, ROLE_CRUD,
            LIST_OF_USERS, USER_CRUD
        );
    }
}
