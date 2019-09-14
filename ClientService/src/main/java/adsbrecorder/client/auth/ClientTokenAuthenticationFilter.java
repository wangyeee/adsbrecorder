package adsbrecorder.client.auth;

import static java.util.Objects.requireNonNull;

import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.service.RemoteReceiverService;
import adsbrecorder.common.auth.ListOfAudiences;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;
import io.jsonwebtoken.Claims;

@Component
public class ClientTokenAuthenticationFilter extends TokenAuthenticationFilter implements ListOfAudiences {

    private RemoteReceiverService receiverService;
    private UserService userService;

    @Autowired
    public ClientTokenAuthenticationFilter(RemoteReceiverService receiverService, UserService userService) {
        this.receiverService = requireNonNull(receiverService);
        this.userService = requireNonNull(userService);
    }

    @Override
    protected Authentication tryAuthenticate(Claims claims) {
        if (REMOTE_RECEIVER.equals(claims.getAudience())) {
            RemoteReceiver receiver = receiverService.findRemoteReceiver(claims.getSubject());
            return receiver.toAuthenticationToken();
        }
        if (USER.equals(claims.getAudience())) {
            User user = userService.findUserByName(claims.getSubject());
            return user.toAuthenticationToken();
        }
        return new UsernamePasswordAuthenticationToken(null, null);
    }

    @Override
    protected Key getSigningKey() {
        // clients only call login api which doesn't require authentication
        return userService.getSecretSigningKey();
    }
}
