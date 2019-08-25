package adsbrecorder.common.auth;

import java.io.IOException;
import java.security.Key;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public abstract class TokenAuthenticationFilter extends OncePerRequestFilter {

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.replace("Bearer ", "");
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(this.getSigningKey())
                        .parseClaimsJws(token)
                        .getBody();
                Authentication authToken = this.tryAuthenticate(claims);
                if (authToken.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                e.printStackTrace(); // TODO syserr
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    protected abstract Authentication tryAuthenticate(Claims claims);

    protected abstract Key getSigningKey();
}
