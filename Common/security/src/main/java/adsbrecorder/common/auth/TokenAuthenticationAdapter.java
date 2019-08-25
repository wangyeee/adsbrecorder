package adsbrecorder.common.auth;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import adsbrecorder.common.utils.URLUtils;

public abstract class TokenAuthenticationAdapter extends WebSecurityConfigurerAdapter implements URLUtils {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AbstractRequestMatcherRegistry<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl> reg;
        reg = http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> {
                e.printStackTrace();
                rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            })
            .and()
            .addFilterAfter(getTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests();
        permitAllURLs().forEach(url -> reg.antMatchers(urlWildcard(url)).permitAll());
        hasAnyAuthorityURLs().forEach((url, auth) -> reg.antMatchers(urlWildcard(url)).hasAnyAuthority(auth));
        reg.anyRequest().authenticated();
    }

    protected abstract TokenAuthenticationFilter getTokenAuthenticationFilter();

    protected Collection<String> permitAllURLs() {
        return List.of();
    }

    protected Map<String, String> hasAnyAuthorityURLs() {
        return Map.of();
    }
}
