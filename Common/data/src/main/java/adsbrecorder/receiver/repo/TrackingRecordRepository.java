package adsbrecorder.receiver.repo;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import adsbrecorder.receiver.entity.TrackingRecord;

public interface TrackingRecordRepository extends MongoRepository<TrackingRecord, BigInteger> {

    List<TrackingRecord> findAllByFlight(String flight);
    List<TrackingRecord> findAllByFlightAndRecordDateGreaterThan(String flight, Date startDate);
    List<TrackingRecord> findAllByFlightAndRecordDateLessThan(String flight, Date endDate);
    List<TrackingRecord> findAllByFlightAndRecordDateBetween(String flight, Date startDate, Date endDate);

    @Query("{flight: ?0, lastTimeSeen: {$gt: ?1, $lt: ?2}}")
    List<TrackingRecord> findAllByFlightAndLastTimeSeenBetween(String flight, long startTime, long endTime);

    @Query("{flight: null}")
    List<TrackingRecord> findAllNullCallsign();

    @Query("{addressICAO: ?0, flight: null}")
    List<TrackingRecord> findAllNullCallsign(int icao);

    @Query("{addressICAO: ?0, lastTimeSeen: {$gt: ?1, $lt: ?2}}")
    Page<TrackingRecord> llfindAllByICAOAddressAndLastSeenBetween(int addr, long startDate, long endDate, Pageable pageable);

    @Query("{recordDate: {$gt: ?0, $lt: ?1}}")
    Page<TrackingRecord> findAllBetweenDates(Date startDate, Date endDate, Pageable pageable);

    default List<TrackingRecord> findAllOnDate(Date day) {
        PageRequest page = PageRequest.of(0, Integer.MAX_VALUE, new Sort(Sort.Direction.DESC, "addressICAO"));
        return findAllBetweenDates(day, new Date(day.getTime() + 1000 * 3600 * 24L), page).getContent();
    }

    default List<TrackingRecord> llfindAllByICAOAddressAndLastSeenBetween(int addr, long startDate, long endDate) {
        PageRequest page = PageRequest.of(0, Integer.MAX_VALUE, new Sort(Sort.Direction.DESC, "lastTimeSeen"));
        return llfindAllByICAOAddressAndLastSeenBetween(addr, startDate, endDate, page).getContent();
    }

    @Query("{addressICAO: ?0, lastTimeSeen: {$lt: ?1}, flight: {$exists: true, $ne: null}}")
    Page<TrackingRecord> findLatestTrackingRecords(int icao, long lastTime, Pageable pageable);

    default String findCallsign(int icao, long lastRecordTime) {
        PageRequest page = PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "lastTimeSeen"));
        Page<TrackingRecord> tr = findLatestTrackingRecords(icao, lastRecordTime, page);
        try {
            TrackingRecord t = tr.getContent().get(0);
            return t.getFlight();
        } catch (Exception e) {
            return "No Callsign";
        }
    }

    default String findCallsign(int icao) {
        return findCallsign(icao, System.currentTimeMillis());
    }
}
