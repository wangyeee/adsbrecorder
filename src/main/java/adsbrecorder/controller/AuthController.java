package adsbrecorder.controller;

import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.entity.RemoteReceiver;
import adsbrecorder.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@RestController
public class AuthController {

    private AuthService authService;

    @Value("${adsbrecorder.client.issuer}")
    private String issuer;

    @Value("${adsbrecorder.client.valid_period:86400000}")
    private long validPeriod;

    public AuthController(AuthService authService) {
        this.authService = requireNonNull(authService);
    }

    @GetMapping("/api/client/login")
    public Map<String, String> clientLogin(@RequestParam(value="name") String name,
            @RequestParam(value="key") String key) {
        RemoteReceiver receiver = authService.authenticate(name, key);
        if (receiver.toAuthenticationToken().isAuthenticated()) {
            long now = System.currentTimeMillis();
            return Map.of("token", Jwts.builder()
                    .setIssuer(issuer)
                    .setAudience(receiver.getRemoteReceiverName())
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + validPeriod))
                    .setId(UUID.randomUUID().toString())
                    .signWith(authService.getSigningKey())
                    .compact().toString());
        }
        return Map.of("message", "Authentication failure");
    }

    // test only
    @PostMapping("/api/jwt")
    public Map<String, String> sign(@RequestBody Map<String, Object> claims) {
        String jws = authService.sign(claims).compact();
        return Map.of("token", jws);
    }

    // test only
    @GetMapping("/api/par")
    public Map<String, Object> parser(@RequestParam(name = "token") String jwt) {
        Jws<Claims> jws = authService.parser(jwt);
        return Map.of("decode", jws);
    }

    // test only
    @GetMapping("/api/testclient")
    public ResponseEntity<Map<String, Object>> createTestReceiver(@RequestParam(name = "name", required = true) String name) {
        RemoteReceiver receiver = authService.createRemoteReceiver(name);
        if (receiver.toAuthenticationToken().isAuthenticated())
            return ResponseEntity.ok(Map.of("receiver", receiver));
        return new ResponseEntity<Map<String, Object>>(Map.of("message", String.format("Client name %s is already used", name)), HttpStatus.BAD_REQUEST);
    }
}
