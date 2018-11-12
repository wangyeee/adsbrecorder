package adsbrecorder.service;

import java.security.Key;

import adsbrecorder.entity.RemoteReceiver;

public interface AuthService {

    RemoteReceiver authenticate(String receiverName, String receiverKey);
    RemoteReceiver createRemoteReceiver(String name);
    RemoteReceiver findRemoteReceiver(String name);
    Key getSigningKey();
}
