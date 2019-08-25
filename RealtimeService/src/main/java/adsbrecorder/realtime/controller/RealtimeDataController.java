package adsbrecorder.realtime.controller;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.common.utils.GeoMathUtils;
import adsbrecorder.realtime.RealtimeServiceMappings;
import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.kafka.ListOfTopics;

@RestController
public class RealtimeDataController implements RealtimeServiceMappings, ListOfTopics, GeoMathUtils {

    private Map<Integer, TrackingRecord> realtimeRecords;
    private KafkaTemplate<String, TrackingRecord> kafkaTemplate;

    @Value("${adsbrecorder.inactive_retention:60000}")
    private long inactiveRetention;

    @Autowired
    public RealtimeDataController(KafkaTemplate<String, TrackingRecord> kafkaTemplate) {
        this.kafkaTemplate = requireNonNull(kafkaTemplate);
        this.realtimeRecords = new ConcurrentHashMap<Integer, TrackingRecord>();
    }

    @Scheduled(fixedDelay = 10000) // 10s
    public void cleanCache() {
        final long now = System.currentTimeMillis();
        Map<Integer, TrackingRecord> temp = new ConcurrentHashMap<Integer, TrackingRecord>(realtimeRecords.size() * 2);
        this.realtimeRecords.values().forEach(record -> {
            if (now - record.getRecordDate().getTime() < this.inactiveRetention) {
                temp.put(record.getAddressICAO(), record);
            } else {
                System.err.println(String.format("Interleave record: %d (%s) @ %d", record.getAddressICAO(), record.getFlight(), record.getLastTimeSeen())); // TODO remove
                kafkaTemplate.send(INTERLEAVING_RECORDS, record);
            }
        });
        this.realtimeRecords = temp;
    }

    @KafkaListener(topics = REALTIME_DATA, groupId = "1")
    public void receiveRealtimeRecords(TrackingRecord record) {
        record.setSourceReceiver(null);
        Integer icao = record.getAddressICAO();
        if (this.realtimeRecords.containsKey(icao)) {
            TrackingRecord prev = this.realtimeRecords.get(icao);
            if (record.getHeading() == 0 || record.getHeading() == 360) {
                record.setHeading((int) calcHeading(prev, record));
            }
            if (record.getVelocity() == 0) {
                record.setVelocity((int) mpsToKnots(calcGroundSpeed(prev, record)));
            }
            if (record.getVerticalRate() == 0) {
                record.setVerticalRate((int) calcVerticalRate(prev, record));
            }
        }
        this.realtimeRecords.put(icao, record);
    }

    @GetMapping(GET_REALTIME_DATA)
    public Collection<TrackingRecord> realtimeData() {
        return this.realtimeRecords.values();
    }
}
