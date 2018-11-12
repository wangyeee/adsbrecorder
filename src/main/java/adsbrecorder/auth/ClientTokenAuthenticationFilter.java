package adsbrecorder.auth;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import adsbrecorder.entity.RemoteReceiver;
import adsbrecorder.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class ClientTokenAuthenticationFilter extends OncePerRequestFilter {

    private AuthService authService;

    @Autowired
    public ClientTokenAuthenticationFilter(AuthService authService) {
        this.authService = requireNonNull(authService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.replace("Bearer ", "");
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(authService.getSigningKey())
                        .parseClaimsJws(token)
                        .getBody();
                RemoteReceiver receiver = authService.findRemoteReceiver(claims.getSubject());
                UsernamePasswordAuthenticationToken authToken = receiver.toAuthenticationToken();
                if (authToken.isAuthenticated())
                    SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
