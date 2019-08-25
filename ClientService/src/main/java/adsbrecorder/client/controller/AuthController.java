package adsbrecorder.client.controller;

import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.client.ClientServiceMappings;
import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.service.AuthService;
import adsbrecorder.common.auth.ListOfAudiences;
import io.jsonwebtoken.Jwts;

@RestController
public class AuthController implements ClientServiceMappings, ListOfAudiences {

    private AuthService authService;

    @Value("${adsbrecorder.client.issuer}")
    private String issuer;

    @Value("${adsbrecorder.client.valid_period:86400000}")
    private long validPeriod;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = requireNonNull(authService);
    }

    @PostMapping(CLIENT_LOGIN)
    public ResponseEntity<Map<String, String>> clientLogin(@RequestParam(value="name") String name,
            @RequestParam(value="key") String key) {
        RemoteReceiver receiver = authService.authenticate(name, key);
        if (receiver.toAuthenticationToken().isAuthenticated()) {
            long now = System.currentTimeMillis();
            return ResponseEntity.status(HttpStatus.OK).body(
                Map.of("token", Jwts.builder()
                            .setIssuer(issuer)
                            .setSubject(receiver.getRemoteReceiverName())
                            .setAudience(REMOTE_RECEIVER)
                            .setIssuedAt(new Date(now))
                            .setExpiration(new Date(now + validPeriod))
                            .setId(UUID.randomUUID().toString())
                            .signWith(authService.getSigningKey())
                            .compact().toString(),
                       "expiration", String.valueOf(now + validPeriod)));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Authentication failure"));
    }
}
