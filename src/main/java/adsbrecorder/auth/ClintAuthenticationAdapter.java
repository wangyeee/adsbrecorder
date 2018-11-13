package adsbrecorder.auth;

import static java.util.Objects.requireNonNull;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@EnableWebSecurity
@Component
public class ClintAuthenticationAdapter extends WebSecurityConfigurerAdapter {

    private ClientTokenAuthenticationFilter authenticationFilter;

    public ClintAuthenticationAdapter(ClientTokenAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = requireNonNull(authenticationFilter);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
            .addFilterAfter(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/live").permitAll()
            .antMatchers("/map").permitAll()
            .antMatchers("/air").permitAll()
            .antMatchers("/main.css").permitAll()
            .antMatchers("/plane.png").permitAll()
            .antMatchers("/built/**").permitAll()
            .antMatchers("/api/**").permitAll()
            .antMatchers("/remote/**").hasAuthority("ADD_TRACKING_RECORD")
            //.antMatchers(HttpMethod.POST, "/remote/**").hasAuthority("ADD_TRACKING_RECORD")
            .anyRequest().authenticated();
    }
}
