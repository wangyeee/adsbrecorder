package adsbrecorder.service;

import java.util.List;

import adsbrecorder.entity.Flight;

public interface FlightService {

    Flight findOrCreateByCallsign(String flightNumber);
    List<Flight> listFlights(int page, int amount);
}
