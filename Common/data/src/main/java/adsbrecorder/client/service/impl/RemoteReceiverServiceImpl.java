package adsbrecorder.client.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.repo.RemoteReceiverRepository;
import adsbrecorder.client.service.RemoteReceiverService;
import adsbrecorder.user.entity.User;

@Service
public class RemoteReceiverServiceImpl implements RemoteReceiverService {

    private RemoteReceiverRepository remoteReceiverRepository;

    @Autowired
    public RemoteReceiverServiceImpl(RemoteReceiverRepository remoteReceiverRepository) {
        this.remoteReceiverRepository = requireNonNull(remoteReceiverRepository);
    }

    @Override
    public RemoteReceiver createRemoteReceiver(String name, String description, User owner) {
        if (remoteReceiverRepository.findOneByRemoteReceiverName(name).isPresent())
            return RemoteReceiver.unAuthorizedReceiver();
        RemoteReceiver receiver = new RemoteReceiver();
        receiver.setRemoteReceiverName(name);
        receiver.setOwner(owner);
        receiver.setDescription(description);
        receiver.setRemoteReceiverKey(UUID.randomUUID().toString());
        return remoteReceiverRepository.save(receiver);
    }

    @Override
    public RemoteReceiver findRemoteReceiver(Long id) {
        Optional<RemoteReceiver> receiver = remoteReceiverRepository.findById(id);
        if (receiver.isPresent())
            return receiver.get();
        return RemoteReceiver.unAuthorizedReceiver();
    }

    @Override
    public RemoteReceiver findRemoteReceiver(String name) {
        Optional<RemoteReceiver> receiver = remoteReceiverRepository.findOneByRemoteReceiverName(name);
        if (receiver.isPresent())
            return receiver.get();
        return RemoteReceiver.unAuthorizedReceiver();
    }

    @Override
    public RemoteReceiver updateRemoteReceiver(RemoteReceiver updateReceiver) {
        if (updateReceiver.getRemoteReceiverID() > 0) {
            return remoteReceiverRepository.save(updateReceiver);
        }
        Optional<RemoteReceiver> receiver = remoteReceiverRepository.findOneByRemoteReceiverName(updateReceiver.getRemoteReceiverName());
        if (receiver.isPresent()) {
            updateReceiver.setRemoteReceiverID(receiver.get().getRemoteReceiverID());
            return remoteReceiverRepository.save(updateReceiver);
        }
        return null;
    }

    @Override
    public List<RemoteReceiver> findByOwner(User user) {
        return remoteReceiverRepository.findAllByOwner(user);
    }

    @Override
    public void removeRemoteReceiver(RemoteReceiver receiver) {
        if (receiver.getRemoteReceiverID() > 0L) {
            remoteReceiverRepository.delete(receiver);
        }
    }
}
