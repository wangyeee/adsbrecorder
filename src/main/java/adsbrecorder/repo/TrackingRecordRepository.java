package adsbrecorder.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import adsbrecorder.entity.TrackingRecord;

public interface TrackingRecordRepository extends JpaRepository<TrackingRecord, Long> {

    @Query("select t from TrackingRecord t join t.flight f where f.flightNumber = :fn order by t.recordDate desc")
    List<TrackingRecord> findByFlightNumber(@Param("fn") String flightNumber, Pageable limit);

    @Query("select t from TrackingRecord t join t.flight f where f.flightNumber = :fn and t.recordDate > :dt")
    List<TrackingRecord> findByFlightNumber(@Param("fn") String flightNumber, @Param("dt") Date date);

    default TrackingRecord findLatestByFlightNumber(String flightNumber) {
        List<TrackingRecord> t = findByFlightNumber(flightNumber, PageRequest.of(0, 1));
        if (t.isEmpty())
            return null;
        return t.get(0);
    }
}
