package adsbrecorder.service.impl;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import adsbrecorder.entity.Flight;
import adsbrecorder.repo.FlightRepository;
import adsbrecorder.service.FlightService;

@Service
public class FlightServiceImpl implements FlightService {

    private FlightRepository flightRepo;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepo) {
        this.flightRepo = requireNonNull(flightRepo);
    }

    @Override
    public Flight findOrCreateByCallsign(String flightNumber) {
        return null;
    }

    @Override
    public List<Flight> listFlights(int page, int amount) {
        return flightRepo.findLatest(PageRequest.of(page, amount));
    }

    @Override
    public List<Flight> findOnDate(Date day, int page, int amount) {
        LocalDateTime start = LocalDateTime.of(day.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MIDNIGHT);
        LocalDateTime end = start.plusDays(1);
        return flightRepo.findByDateRange(Date.from(start.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(end.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()), PageRequest.of(page, amount));
    }
}
