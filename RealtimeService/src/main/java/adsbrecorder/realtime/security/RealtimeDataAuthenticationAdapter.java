package adsbrecorder.realtime.security;

import java.security.Key;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import adsbrecorder.common.auth.TokenAuthenticationAdapter;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.realtime.RealtimeServiceMappings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

@EnableWebSecurity
@Component
public class RealtimeDataAuthenticationAdapter extends TokenAuthenticationAdapter implements RealtimeServiceMappings {

    private TokenAuthenticationFilter tokenAuthenticationFilter;

    public RealtimeDataAuthenticationAdapter() {
        this.tokenAuthenticationFilter = new TokenAuthenticationFilter() {
            final private byte key0[] = new byte[32];
            @Override
            protected Authentication tryAuthenticate(Claims claims) {
                return new UsernamePasswordAuthenticationToken(null, null, List.of(new GrantedAuthority() {
                    private static final long serialVersionUID = -8838421554605816988L;
                    @Override
                    public String getAuthority() {
                        return "DUMMY";
                    }
                }));
            }
            @Override
            protected Key getSigningKey() {
                return Keys.hmacShaKeyFor(key0);
            }
        };
    }

    @Override
    protected TokenAuthenticationFilter getTokenAuthenticationFilter() {
        return tokenAuthenticationFilter;
    }

    @Override
    protected Collection<String> permitAllURLs() {
        return List.of(GET_REALTIME_DATA);
    }
}
