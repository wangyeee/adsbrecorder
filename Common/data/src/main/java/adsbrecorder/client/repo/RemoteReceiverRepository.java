package adsbrecorder.client.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.user.entity.User;

public interface RemoteReceiverRepository extends JpaRepository<RemoteReceiver, Long> {

    Optional<RemoteReceiver> findOneByRemoteReceiverName(String remoteReceiverName);

    @Query("select r from RemoteReceiver r where r.remoteReceiverName = :name and r.remoteReceiverKey = :key")
    Optional<RemoteReceiver> findOneByRemoteReceiverNameAndKey(
            @Param("name") String remoteReceiverName,
            @Param("key") String remoteReceiverKey);

    Optional<RemoteReceiver> findOneByRemoteReceiverKey(String remoteReceiverKey);
    List<RemoteReceiver> findAllByOwner(User owner);
}
