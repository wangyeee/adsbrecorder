package adsbrecorder.common.auth;

import java.io.IOException;
import java.security.Key;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public abstract class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final static String jwtCookieName = "JWT-AUTH";

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null) {
             Cookie[] cookies = request.getCookies();
             if (cookies!= null) {
                 for (Cookie cookie : cookies) {
                     if (jwtCookieName.equals(cookie.getName())) {
                         authenticateToken(cookie.getValue());
                         break;
                     }
                 }
             }
        } else if (header.startsWith("Bearer ")) {
            authenticateToken(header.replace("Bearer ", ""));
        }
        chain.doFilter(request, response);
    }

    private void authenticateToken(String token) {
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
            SecurityContextHolder.clearContext();
        }
    }

    protected abstract Authentication tryAuthenticate(Claims claims);

    protected abstract Key getSigningKey();

    public final static Cookie generateInvalidCookie() {
        Cookie cookie = defaultCookie("");
        cookie.setMaxAge(0);
        return cookie;
    }

    public final static Cookie generateAuthenticationCookie(String token) {
        return defaultCookie(token);
    }

    private final static Cookie defaultCookie(String token) {
        Cookie cookie = new Cookie(jwtCookieName, token);
        cookie.setPath("/api");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
