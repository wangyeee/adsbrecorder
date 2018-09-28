package adsbrecorder.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import adsbrecorder.entity.FlightAirlineRule;

public interface FlightAirlineRuleRepository extends JpaRepository<FlightAirlineRule, Long> {

    FlightAirlineRule findByFlightNumber(String flightNumber);
}
