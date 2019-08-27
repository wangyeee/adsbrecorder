package adsbrecorder.client.service;

import java.util.List;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.user.entity.User;

public interface RemoteReceiverService {
    RemoteReceiver createRemoteReceiver(String name, String description, User owner);
    RemoteReceiver updateRemoteReceiver(RemoteReceiver receiver);
    RemoteReceiver findRemoteReceiver(Long id);
    RemoteReceiver findRemoteReceiver(String name);
    List<RemoteReceiver> findByOwner(User user);
}
