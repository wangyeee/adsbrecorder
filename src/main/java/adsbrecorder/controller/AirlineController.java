package adsbrecorder.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.entity.Airline;
import adsbrecorder.entity.Flight;
import adsbrecorder.service.AirlineService;
import adsbrecorder.service.FlightService;

@RestController
public class AirlineController {

    @Value("${adsbrecorder.google_map_key}")
    private String googleMapKey;

    @Value("${adsbrecorder.receiver_loc_lati}")
    private String receiverLatitude;

    @Value("${adsbrecorder.receiver_loc_long}")
    private String receiverLongitude;

    @Value("${adsbrecorder.receiver_loc_alti}")
    private String receiverAltitude;

    private Map<String, String> receiverLocation;

    private AirlineService airlineService;

    private FlightService flightService;

    @Autowired
    public AirlineController(AirlineService airlineService, FlightService flightService) {
        this.airlineService = Objects.requireNonNull(airlineService);
        this.flightService = Objects.requireNonNull(flightService);
        this.receiverLocation = null;
    }

    // test only
    @GetMapping("/api/randair")
    public List<Airline> randomAirlines(@RequestParam(value="n", defaultValue = "5") String amountStr) {
        Random rand = new Random();
        int x = 6178;
        int n;
        try {
            n = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            n = 5;
        }
        List<Long> ids = new ArrayList<Long>(n);
        while (n > 0) {
            ids.add(Long.valueOf(rand.nextInt(x)));
            n--;
        }
        return airlineService.findByIds(ids);
    }

    @GetMapping("/api/gmap")
    public String getGoogleMapKey() {
        return String.format("{\"key\" : \"%s\"}", googleMapKey);
    }

    @GetMapping("/api/rloc")
    public Map<String, String> getReceiverLocationMap() {
        if (this.receiverLocation == null) {
            this.receiverLocation = new HashMap<String, String>();;
            this.receiverLocation.put("lati", receiverLatitude);
            this.receiverLocation.put("long", receiverLongitude);
            this.receiverLocation.put("alti", receiverAltitude);
        }
        return this.receiverLocation;
    }

    @GetMapping("/api/flights")
    public List<Flight> listFlights(@RequestParam(value="n", defaultValue = "5") String amountStr) {
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            amount = 5;
        }
        if (amount <= 0)
            amount = 5;
        return flightService.listFlights(0, amount);
    }
}
