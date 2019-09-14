package adsbrecorder.client.auth;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

import adsbrecorder.client.ClientServiceMappings;
import adsbrecorder.common.auth.ListOfAuthorities;
import adsbrecorder.common.auth.TokenAuthenticationAdapter;
import adsbrecorder.common.auth.TokenAuthenticationFilter;

@EnableWebSecurity
@Component
public class ClientAuthenticationAdapter extends TokenAuthenticationAdapter implements ListOfAuthorities, ClientServiceMappings {

    private ClientTokenAuthenticationFilter authenticationFilter;

    @Autowired
    public ClientAuthenticationAdapter(ClientTokenAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = requireNonNull(authenticationFilter);
    }

    @Override
    protected TokenAuthenticationFilter getTokenAuthenticationFilter() {
        return this.authenticationFilter;
    }

    @Override
    protected Collection<String> permitAllURLs() {
        return List.of(CLIENT_LOGIN, LIST_USER_CLIENTS);
    }

    @Override
    protected Map<String, String> hasAnyAuthorityURLs() {
        return Map.of(CLIENT_NEW, WRITE_REMOTE_RECEIVER,
                CLIENT_UPDATE_DESCRIPTION, WRITE_REMOTE_RECEIVER,
                CLIENT_UPDATE_KEY, WRITE_REMOTE_RECEIVER,
                CLIENT_EXPORT, WRITE_REMOTE_RECEIVER,
                CLIENT_REMOVAL, WRITE_REMOTE_RECEIVER,
                CLIENT_UPDATE, WRITE_REMOTE_RECEIVER);
    }
}
