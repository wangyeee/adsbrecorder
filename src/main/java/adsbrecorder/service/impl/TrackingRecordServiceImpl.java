package adsbrecorder.service.impl;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import adsbrecorder.entity.Airline;
import adsbrecorder.entity.Flight;
import adsbrecorder.entity.FlightAirlineRule;
import adsbrecorder.entity.TrackingRecord;
import adsbrecorder.repo.AirlineRepository;
import adsbrecorder.repo.FlightAirlineRuleRepository;
import adsbrecorder.repo.FlightRepository;
import adsbrecorder.repo.TrackingRecordRepository;
import adsbrecorder.service.TrackingRecordService;

@Service
public class TrackingRecordServiceImpl implements TrackingRecordService {

    @Value("${adsbrecorder.inactive_retention:60000}")
    private long inactiveRetention;
    
    private Map<String, TrackingRecord> latestRecords;

    private TrackingRecordRepository recordRepo;

    private FlightRepository flightRepo;

    private FlightAirlineRuleRepository ruleRepo;

    private AirlineRepository airlineRepo;

    private boolean receiver;

    @Autowired
    public TrackingRecordServiceImpl(TrackingRecordRepository recordRepo, FlightRepository flightRepo,
            FlightAirlineRuleRepository ruleRepo, AirlineRepository airlineRepo) {
        latestRecords = new ConcurrentHashMap<String, TrackingRecord>();
        this.recordRepo = requireNonNull(recordRepo);
        this.flightRepo = requireNonNull(flightRepo);
        this.ruleRepo = requireNonNull(ruleRepo);
        this.airlineRepo = requireNonNull(airlineRepo);
    }

    @Scheduled(fixedDelay = 10000) // 10s
    public void cleanCache() {
        long now = System.currentTimeMillis();
        Map<String, TrackingRecord> temp = new ConcurrentHashMap<String, TrackingRecord>(latestRecords.size() * 2);
        for (TrackingRecord record : latestRecords.values()) {
            if (now - record.getRecordDate().getTime() < inactiveRetention) {
                temp.put(record.getFlight().getFlightNumber(), record);
            }
        }
        latestRecords = temp;
    }

    @Override
    public void addRecord(TrackingRecord record) {
        String flightNumber = record.getFlight().getFlightNumber();
        if (StringUtils.isEmpty(flightNumber)) {
            // ignore flights with empty flight number ?
            return;
        }
        TrackingRecord prev = latestRecords.get(flightNumber);
        if (!record.tooClose(prev)) {
            // First check if the same flight number has been recorded before
            Flight f = flightRepo.findByFlightNumber(flightNumber);
            if (f == null) {
                f = new Flight();
                // Check if there is any pre-defined rule against the flight number
                FlightAirlineRule rule = ruleRepo.findByFlightNumber(flightNumber);
                Airline a;
                if (rule == null) {
                    int i;
                    for (i = 0; i < flightNumber.length(); i++) {
                        if (flightNumber.charAt(i) >= '0' && flightNumber.charAt(i) <= '9')
                            break;
                    }
                    // ABC123 -> ABC, search 'ABC' in airline table
                    a = airlineRepo.findByIATAorICAO(flightNumber.substring(0, i));
                    if (a == null) {
                        // Giveup...
                        a = new Airline();
                        a.setCountry("ZZ");
                        a.setICAO(flightNumber.substring(0, i));
                        a.setName("Unknown");
                        a.setComments("New found.");
                        airlineRepo.save(a);
                    }
                } else {
                    a = rule.getAirline();
                }
                f.setFlightNumber(flightNumber);
                f.setAirline(a);
                flightRepo.save(f);
            }
            record.setFlight(f);
            recordRepo.save(record);
            latestRecords.put(flightNumber, record);
        }
    }

    @Override
    public TrackingRecord latestRecord(String flightNumber) {
        // Try to find in cache first
        TrackingRecord t = latestRecords.get(flightNumber);
        if (t != null)
            return t;
        // then search the database
        t = recordRepo.findLatestByFlightNumber(flightNumber);
        return t;
    }

    @Override
    public List<TrackingRecord> trackingHistory(String flightNumber, int page, int amount) {
        return recordRepo.findByFlightNumber(flightNumber, PageRequest.of(page, amount));
    }

    @Override
    public Map<String, TrackingRecord> getLiveRecords() {
        return latestRecords;
    }

    @Override
    public List<TrackingRecord> getTrackBetween(String flightNumber, Date after, Date before) {
        return recordRepo.findByFlightNumberInDateRange(flightNumber, after, before);
    }

    @Override
    public List<TrackingRecord> getTrackBefore(String flightNumber, Date before) {
        return recordRepo.findByFlightNumberInDateRange(flightNumber, new Date(0L), before);
    }

    @Override
    public List<TrackingRecord> getTrackAfter(String flightNumber, Date after) {
        return recordRepo.findByFlightNumberInDateRange(flightNumber, after, new Date());
    }

    @Override
    public List<TrackingRecord> getTrackOn(String flightNumber, Date date) {
        LocalDateTime start = LocalDateTime.of(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalTime.MIDNIGHT);
        LocalDateTime end = start.plusDays(1);
        return recordRepo.findByFlightNumberInDateRange(flightNumber,
                Date.from(start.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(end.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public List<TrackingRecord> getLiveTrack(String flightNumber) {
        TrackingRecord t = latestRecords.get(flightNumber);
        if (t != null) {
            return getTrackOn(flightNumber, t.getRecordDate());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Date> findDatesWithAnyFlight(int page, int amount) {
        return recordRepo.findDatesWithAnyFlight(PageRequest.of(page, amount));
    }

    @Override
    public List<Date> findDatesWithFlight(String flightNumber, int page, int amount) {
        return recordRepo.findDatesWithFlight(flightNumber, PageRequest.of(page, amount));
    }

    @Override
    public void setLocalReceiver(boolean receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean hasLocalReceiver() {
        return this.receiver;
    }
}
