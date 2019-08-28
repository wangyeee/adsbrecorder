package adsbrecorder.user.controller;

import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.common.aop.LoginUser;
import adsbrecorder.common.aop.RequireLogin;
import adsbrecorder.common.auth.ListOfAudiences;
import adsbrecorder.common.auth.TokenAuthenticationFilter;
import adsbrecorder.user.UserServiceMappings;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;
import io.jsonwebtoken.Jwts;

@RestController
public class UserController implements UserServiceMappings, ListOfAudiences {

    private UserService userService;

    @Value("${adsbrecorder.userservice.issuer}")
    private String issuer;

    @Value("${adsbrecorder.userservice.valid_period:86400000}")
    private long validPeriod;

    @Autowired
    public UserController(UserService userService) {
        this.userService = requireNonNull(userService);
    }

    @PostMapping(USER_NEW)
    public ResponseEntity<Map<String, Object>> userRegister(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password) {
        boolean usernameExist = userService.isUsernameExist(username);
        if (usernameExist) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", String.format("Username %s already exists.", username)));
        }
        User newUser = userService.createNewUser(username, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("newUser", newUser));
    }

    @GetMapping(USERNAME_CHECK)
    public ResponseEntity<Map<String, String>> usernameExistsCheck(
            @RequestParam(value = "username", required = true) String username) {
        boolean usernameExist = userService.isUsernameExist(username);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "username", username,
                "exist", Boolean.toString(usernameExist)));
    }

    /**
     * Restore login user instance from JWT token
     * @param user the login user instance resolved by <code>adsbrecorder.common.aop.LoginUserArgumentResolver</code>
     * @return the login user as <code>org.springframework.http.ResponseEntity</code>
     */
    @RequireLogin
    @GetMapping(USER_LOGIN)
    public ResponseEntity<Map<String, Object>> userLoginFromJWT(@LoginUser User user) {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("user", user));
    }

    @PostMapping(USER_LOGIN)
    public ResponseEntity<Map<String, Object>> userLogin(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "cookie", required = false, defaultValue = "true") boolean setCookie,
            HttpServletResponse response) {
        User user = userService.login(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Username and password mismatch."));
        }
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setIssuer(issuer)
                .setAudience(USER)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validPeriod))
                .setId(UUID.randomUUID().toString())
                .signWith(userService.getSecretSigningKey())
                .compact().toString();
        if (setCookie) {
            Cookie cookie = TokenAuthenticationFilter.generateAuthenticationCookie(token);
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("user", user));
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("user", user, "token", token));
    }
}
