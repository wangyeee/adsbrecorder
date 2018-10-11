package adsbrecorder.service;

import java.util.Date;
import java.util.List;

import adsbrecorder.entity.Flight;

public interface FlightService {

    Flight findOrCreateByCallsign(String flightNumber);
    List<Flight> listFlights(int page, int amount);
    List<Flight> findOnDate(Date day, int page, int amount);
}
