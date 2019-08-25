package adsbrecorder.data.security;

import static java.util.Objects.requireNonNull;

import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.service.AuthService;
import adsbrecorder.client.service.RemoteReceiverService;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.user.service.UserService;
import io.jsonwebtoken.Claims;

@Component
public class DataTokenAuthenticationFilter extends TokenAuthenticationFilter {

    private AuthService authService;
    private RemoteReceiverService remoteReceiverService;
    private UserService userService;

    @Autowired
    public DataTokenAuthenticationFilter(AuthService authService, RemoteReceiverService remoteReceiverService, UserService userService) {
        this.authService = requireNonNull(authService);
        this.remoteReceiverService = requireNonNull(remoteReceiverService);
        this.userService = requireNonNull(userService);
    }

    @Override
    protected Authentication tryAuthenticate(Claims claims) {
        RemoteReceiver receiver = remoteReceiverService.findRemoteReceiver(claims.getSubject());
        // Inherit authorities from owner
        receiver.setOwner(userService.authorize(receiver.getOwner()));
        return receiver.toAuthenticationToken();
    }

    @Override
    protected Key getSigningKey() {
        return authService.getSigningKey();
    }
}
