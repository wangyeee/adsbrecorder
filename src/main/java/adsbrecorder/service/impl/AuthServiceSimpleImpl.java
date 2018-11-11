package adsbrecorder.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import adsbrecorder.service.AuthService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthServiceSimpleImpl implements AuthService {

    @Value("${adsbrecorder.client.sha_key}")
    private String shaKeyStr;

    private transient byte[] shaKey;

    @PostConstruct
    public void decodeKey() {
        shaKey = Decoders.BASE64.decode(requireNonNull(shaKeyStr));
    }

    @Override
    public JwtBuilder sign(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(shaKey), SignatureAlgorithm.HS256);
    }
}
