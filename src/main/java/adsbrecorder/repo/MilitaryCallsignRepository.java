package adsbrecorder.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import adsbrecorder.entity.MilitaryCallsign;

public interface MilitaryCallsignRepository extends JpaRepository<MilitaryCallsign, Long> {

    Optional<MilitaryCallsign> findByCallsign(String callsign);
}
