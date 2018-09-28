package adsbrecorder.service.impl;

import java.util.List;
import java.util.Objects;

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
        this.flightRepo = Objects.requireNonNull(flightRepo);
    }

    @Override
    public Flight findOrCreateByCallsign(String flightNumber) {
        return null;
    }

    @Override
    public List<Flight> listFlights(int page, int amount) {
        return flightRepo.findLatest(PageRequest.of(page, amount));
    }
}
