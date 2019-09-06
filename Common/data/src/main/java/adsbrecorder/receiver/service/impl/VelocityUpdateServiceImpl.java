package adsbrecorder.receiver.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.repo.RemoteReceiverRepository;
import adsbrecorder.receiver.entity.VelocityUpdate;
import adsbrecorder.receiver.repo.VelocityUpdateRepository;
import adsbrecorder.receiver.service.VelocityUpdateService;

@Service
public class VelocityUpdateServiceImpl implements VelocityUpdateService {

    private VelocityUpdateRepository velocityUpdateRepository;
    private RemoteReceiverRepository remoteReceiverRepository;

    @Value("${adsbrecorder.data.date_range_expn:600000}")
    private long dateRangeExpansion;

    @Autowired
    public VelocityUpdateServiceImpl(VelocityUpdateRepository velocityUpdateRepository,
            RemoteReceiverRepository remoteReceiverRepository) {
        this.velocityUpdateRepository = requireNonNull(velocityUpdateRepository);
        this.remoteReceiverRepository = requireNonNull(remoteReceiverRepository);
    }

    @Override
    public VelocityUpdate addVelocityUpdate(VelocityUpdate update) {
        return velocityUpdateRepository.save(update);
    }

    @Override
    public List<VelocityUpdate> batchCreateVelocityUpdates(Collection<VelocityUpdate> updates,
            String sourceReceiverName,
            String sourceReceiverKey) {
        Optional<RemoteReceiver> sourceReceiver = remoteReceiverRepository.findOneByRemoteReceiverNameAndKey(sourceReceiverName, sourceReceiverKey);
        if (sourceReceiver.isPresent()) {
            updates.forEach(update -> update.setSourceReceiver(sourceReceiver.get()));
            return velocityUpdateRepository.saveAll(updates);
        }
        return List.of();
    }
}
