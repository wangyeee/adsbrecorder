package adsbrecorder.client.service;

import java.security.Key;

import adsbrecorder.client.entity.RemoteReceiver;

public interface AuthService {

    RemoteReceiver authenticate(String receiverName, String receiverKey);
    Key getSigningKey();
}
