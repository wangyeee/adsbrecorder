package adsbrecorder.receiver.repo;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import adsbrecorder.receiver.entity.VelocityUpdate;

public interface VelocityUpdateRepository extends MongoRepository<VelocityUpdate, BigInteger> {

    @Query("{addressICAO: ?0, lastTimeSeen: {$lt: ?1}, applied: ?2}")
    Page<VelocityUpdate> findAllByICAOTimeStatus(int addressICAO, long lastTime, boolean applied, Pageable pageable);

    default List<VelocityUpdate> findAllActiveByAddressICAO(int addressICAO, long lastTime) {
        PageRequest page = PageRequest.of(0, Integer.MAX_VALUE, new Sort(Sort.Direction.DESC, "lastTimeSeen"));
        return findAllByICAOTimeStatus(addressICAO, lastTime, false, page).getContent();
    }

    default List<VelocityUpdate> findAllActiveByAddressICAO(int addressICAO) {
        return findAllActiveByAddressICAO(addressICAO, System.currentTimeMillis());
    }
}
