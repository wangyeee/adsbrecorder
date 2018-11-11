package adsbrecorder.service;

import java.util.Map;

import io.jsonwebtoken.JwtBuilder;

public interface AuthService {

    JwtBuilder sign(Map<String, Object> claims);
}
