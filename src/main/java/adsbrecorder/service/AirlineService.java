package adsbrecorder.service;

import java.util.List;

import adsbrecorder.entity.Airline;

public interface AirlineService {

    long DEFAULT_AIRLINE_ID = 1L;

    boolean checkDefaultAirline();

    boolean checkKnownAirlines();

    void createDefaultAirline();

    void loadKnownAirlines();

    List<Airline> findByIds(List<Long> ids);
}
