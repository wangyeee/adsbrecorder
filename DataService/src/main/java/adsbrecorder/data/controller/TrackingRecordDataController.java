package adsbrecorder.data.controller;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import adsbrecorder.data.DataServiceMappings;
import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.service.TrackingRecordService;
import reactor.core.publisher.Flux;

@RestController
public class TrackingRecordDataController implements DataServiceMappings {

    private TrackingRecordService trackingRecordService;

    @Value("${adsbrecorder.google_map_key}")
    private String googleMapsKey;

    @Value("${adsbrecorder.flux_interval_second:5}")
    private int fluxInterval;

    public TrackingRecordDataController(TrackingRecordService trackingRecordService) {
        this.trackingRecordService = requireNonNull(trackingRecordService);
    }

    @GetMapping(READ_RECENT_FLIGHT)
    public List<Map<String, String>> recentFlights(@RequestParam(name = "n", required = false, defaultValue = "5") int amount) {
        amount = amount > 0 ? amount : 5;
        Map<String, Date> flights = trackingRecordService.getRecentFlights(amount);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return flights.entrySet().stream().map(
                    flight -> Map.of(
                    "flight", flight.getKey(),
                    "date", df.format(flight.getValue())))
                .collect(Collectors.toList());
    }

    @GetMapping(READ_FLIGHT_RECORD)
    public Collection<TrackingRecord> getTrackingRecordForFlight(
            @PathVariable("flight") String flight,
            @RequestParam(name = "start", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
            @RequestParam(name = "end", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return trackingRecordService.findAllByFlightNumber(flight, startDate, endDate);
    }

    @GetMapping(READ_FLIGHT_RECORD_EVENT)
    public Flux<ServerSentEvent<Collection<TrackingRecord>>> trackingRecordForFlightEventSource(
            @PathVariable("flight") String flight,
            @RequestParam(name = "start", required = false) long startTime) {
        List<TrackingRecord> liveRecords = trackingRecordService.findAllByFlightNumber(flight, startTime, System.currentTimeMillis());
        liveRecords.sort((a, b) -> {
            a.setSourceReceiver(null);
            b.setSourceReceiver(null);
            return (int) (a.getLastTimeSeen() - b.getLastTimeSeen());
        });
        return Flux.interval(Duration.ofSeconds(fluxInterval))
                .map(seq -> ServerSentEvent.<Collection<TrackingRecord>> builder()
                        .id(String.valueOf(seq))
                        .event("realtimeFlightTrack")
                        .data(liveRecords).build());
    }

    @GetMapping(GET_TRACK)
    public Collection<TrackingRecord> getTrackingRecordForFlight(
            @PathVariable("flight") String flight,
            @RequestParam(name = "d", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date day) {
        final long oneDay = 1000 * 3600 * 24L;
        List<TrackingRecord> ret = null;
        if (day == null) {
            long now = System.currentTimeMillis();
            ret = trackingRecordService.findAllByFlightNumber(flight, new Date(now - oneDay), new Date(now));
        } else {
            ret = trackingRecordService.findAllByFlightNumber(flight, day, new Date(day.getTime() + oneDay));
        }
        ret = ret.parallelStream().map(tr -> {
            tr.setSourceReceiver(null);
            return tr;
        }).collect(Collectors.toList());
        ret.sort((a, b) -> (int) (a.getLastTimeSeen() - b.getLastTimeSeen()));
        return ret;
    }

    @GetMapping(READ_RECORD)
    public TrackingRecord getTrackingRecord(@PathVariable("id") String id) {
        try {
            return trackingRecordService.findById(new BigInteger(id));
        } catch (Exception e) {
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("No data found for %s", id));
    }

    @GetMapping(READ_GM_KEY)
    public Map<String, Object> getGoogleMapAPIKey() {
        return Map.of("key", googleMapsKey,
                "lati", Double.valueOf(-37.5),
                "long", Double.valueOf(175.33),
                "zoom", Integer.valueOf(8));
    }
}
