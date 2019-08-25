package adsbrecorder.client.service.impl;

import static java.util.Objects.requireNonNull;

import java.security.Key;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.repo.RemoteReceiverRepository;
import adsbrecorder.client.service.AuthService;
import adsbrecorder.user.service.UserService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthServiceSimpleImpl implements AuthService {

    @Value("${adsbrecorder.client.sha_key}")
    private String shaKeyStr;

    private transient byte[] shaKey;

    private RemoteReceiverRepository remoteReceiverRepository;
    private UserService userService;

    @Autowired
    public AuthServiceSimpleImpl(RemoteReceiverRepository remoteReceiverRepository, UserService userService) {
        this.remoteReceiverRepository = requireNonNull(remoteReceiverRepository);
        this.userService = requireNonNull(userService);
    }

    @PostConstruct
    public void decodeKey() {
        shaKey = Decoders.BASE64.decode(requireNonNull(shaKeyStr));
    }

    @Override
    public RemoteReceiver authenticate(String receiverName, String receiverKey) {
        Optional<RemoteReceiver> optReceiver = remoteReceiverRepository.findOneByRemoteReceiverName(receiverName);
        if (optReceiver.isPresent()) {
            RemoteReceiver receiver = optReceiver.get();
            if (receiver.getRemoteReceiverKey().equals(receiverKey)) {
                receiver.setOwner(userService.authorize(receiver.getOwner()));
                return receiver;
            }
        }
        return RemoteReceiver.unAuthorizedReceiver();
    }

    @Override
    public Key getSigningKey() {
        return Keys.hmacShaKeyFor(shaKey);
    }
}
