package adsbrecorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import adsbrecorder.entity.Airline;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {

    @Query("select a from Airline a where a.IATA = :val or a.ICAO = :val")
    Airline findByIATAorICAO(@Param("val") String value);
}
