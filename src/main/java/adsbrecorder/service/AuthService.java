package adsbrecorder.service;

import java.security.Key;
import java.util.Map;

import adsbrecorder.entity.RemoteReceiver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;

public interface AuthService {

    JwtBuilder sign(Map<String, Object> claims);
    Jws<Claims> parser(String jwt);
    RemoteReceiver authenticate(String receiverName, String receiverKey);
    RemoteReceiver createRemoteReceiver(String name);
    Key getSigningKey();
}
