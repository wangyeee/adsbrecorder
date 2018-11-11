package adsbrecorder.service.impl;

import static java.util.Objects.requireNonNull;

import java.security.Key;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import adsbrecorder.entity.RemoteReceiver;
import adsbrecorder.repo.RemoteReceiverRepository;
import adsbrecorder.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthServiceSimpleImpl implements AuthService {

    @Value("${adsbrecorder.client.sha_key}")
    private String shaKeyStr;

    private transient byte[] shaKey;

    private RemoteReceiverRepository remoteReceiverRepository;

    @Autowired
    public AuthServiceSimpleImpl(RemoteReceiverRepository remoteReceiverRepository) {
        this.remoteReceiverRepository = requireNonNull(remoteReceiverRepository);
    }

    @PostConstruct
    public void decodeKey() {
        shaKey = Decoders.BASE64.decode(requireNonNull(shaKeyStr));
    }

    @Override
    public RemoteReceiver authenticate(String receiverName, String receiverKey) {
        Optional<RemoteReceiver> receiver = remoteReceiverRepository.findOneByRemoteReceiverName(receiverName);
        if (receiver.isPresent()) {
            if (receiver.get().getRemoteReceiverKey().equals(receiverKey))
                return receiver.get();
        }
        return RemoteReceiver.unAuthorizedReceiver();
    }

    @Override
    public JwtBuilder sign(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(shaKey));
    }

    @Override
    public Jws<Claims> parser(String jwt) {
        return Jwts.parser()
        .setSigningKeyResolver(new SigningKeyResolverAdapter() {
            @Override
            @SuppressWarnings("rawtypes")
            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                return AuthServiceSimpleImpl.this.shaKey;
            }
        }).parseClaimsJws(jwt);
    }

    @Override
    public RemoteReceiver createRemoteReceiver(String name) {
        if (remoteReceiverRepository.findOneByRemoteReceiverName(name).isPresent())
            return RemoteReceiver.unAuthorizedReceiver();
        RemoteReceiver receiver = new RemoteReceiver();
        receiver.setRemoteReceiverName(name);
        receiver.setRemoteReceiverKey(UUID.randomUUID().toString());
        remoteReceiverRepository.save(receiver);
        return receiver;
    }

    @Override
    public Key getSigningKey() {
        return Keys.hmacShaKeyFor(shaKey);
    }
}
