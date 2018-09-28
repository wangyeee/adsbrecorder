package adsbrecorder.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        this.recordRepo = Objects.requireNonNull(recordRepo);
        this.flightRepo = Objects.requireNonNull(flightRepo);
        this.ruleRepo = Objects.requireNonNull(ruleRepo);
        this.airlineRepo = Objects.requireNonNull(airlineRepo);
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
            System.out.println(record);
            Flight f = flightRepo.findByFlightNumber(flightNumber);
            if (f == null) {
                f = new Flight();
                FlightAirlineRule rule = ruleRepo.findByFlightNumber(flightNumber);
                Airline a;
                if (rule == null) {
                    int i;
                    for (i = 0; i < flightNumber.length(); i++) {
                        if (flightNumber.charAt(i) >= '0' && flightNumber.charAt(i) <= '9')
                            break;
                    }
                    a = airlineRepo.findByIATAorICAO(flightNumber.substring(0, i));
                    if (a == null) {
                        a = new Airline();
                        a.setCountry("ZZ");
                        a.setICAO(flightNumber.substring(0, i));
                        a.setName("Unknown");
                        a.setComments("New found.");
                        airlineRepo.save(a);
                    }
                } else {
                    System.err.println("Using pre-defined flight number rule: " + rule);
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
        TrackingRecord t = latestRecords.get(flightNumber);
        if (t != null)
            return t;
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
    public List<TrackingRecord> getLiveTrack(String flightNumber) {
        // TODO change days
        return recordRepo.findByFlightNumber(flightNumber, new Date(System.currentTimeMillis() - 24 * 3600000L));
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
