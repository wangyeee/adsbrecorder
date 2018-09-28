package adsbrecorder.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import adsbrecorder.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    Flight findByFlightNumber(String flightNumber);

    @Query("select distinct t.flight from TrackingRecord t join t.flight order by t.recordDate desc")
    List<Flight> findLatest(Pageable page);
}
