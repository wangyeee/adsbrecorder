package adsbrecorder.receiver.controller;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.receiver.ReceiverServiceMappings;
import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.entity.VelocityUpdate;
import adsbrecorder.receiver.kafka.ListOfTopics;
import adsbrecorder.receiver.service.TrackingRecordService;
import adsbrecorder.receiver.service.VelocityUpdateService;

@RestController
public class TrackingRecordController implements ReceiverServiceMappings, ListOfTopics {

    private TrackingRecordService trackingRecordService;
    private VelocityUpdateService velocityUpdateService;
    private KafkaTemplate<String, TrackingRecord> kafkaTemplate;

    @Autowired
    public TrackingRecordController(TrackingRecordService trackingRecordService,
            VelocityUpdateService velocityUpdateService,
            KafkaTemplate<String, TrackingRecord> kafkaTemplate) {
        this.trackingRecordService = requireNonNull(trackingRecordService);
        this.velocityUpdateService = requireNonNull(velocityUpdateService);
        this.kafkaTemplate = requireNonNull(kafkaTemplate);
    }

    @PostMapping(ADD_VELOCITY_UPDATES)
    public Map<String, String> createVelocityUpdates(@RequestBody List<VelocityUpdate> updates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        updates = velocityUpdateService.batchCreateVelocityUpdates(updates,
                String.valueOf(auth.getPrincipal()),
                String.valueOf(auth.getCredentials()));
        return Map.of("velocityUpdated", String.valueOf(updates.size()),
                "source", String.valueOf(auth.getPrincipal()));
    }

    @PostMapping(ADD_NEW_RECORDS)
    public Map<String, String> createNewTrackingRecord(@RequestBody List<TrackingRecord> records) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        records = records.stream().filter(record ->
             Math.abs(record.getLatitude()) > 0.0
          && Math.abs(record.getLongitude()) > 0.0
          && Math.abs(record.getAltitude()) > 0.0).collect(Collectors.toList());
        if (records.size() > 0) {
            records = trackingRecordService.batchCreateTrackingRecord(records,
                    String.valueOf(auth.getPrincipal()),
                    String.valueOf(auth.getCredentials()));
            records.forEach(record -> kafkaTemplate.send(REALTIME_DATA, record));
        }
        return Map.of("recordsCreated", String.valueOf(records.size()),
                      "source", String.valueOf(auth.getPrincipal()));
    }
}
