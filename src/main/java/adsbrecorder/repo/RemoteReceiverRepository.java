package adsbrecorder.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import adsbrecorder.entity.RemoteReceiver;

public interface RemoteReceiverRepository extends JpaRepository<RemoteReceiver, Long> {

    Optional<RemoteReceiver> findOneByRemoteReceiverName(String remoteReceiverName);

    Optional<RemoteReceiver> findOneByRemoteReceiverKey(String remoteReceiverKey);
}
