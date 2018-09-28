package adsbrecorder.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.entity.Flight;
import adsbrecorder.entity.TrackingRecord;
import adsbrecorder.service.TrackingRecordService;

@RestController
public class TrackingRecordController {

    private TrackingRecordService trackingRecordService;

    @Autowired
    public TrackingRecordController(TrackingRecordService trackingRecordService) {
        this.trackingRecordService = Objects.requireNonNull(trackingRecordService);
    }

    @GetMapping("/api/{f}/latest")
    public TrackingRecord latestTrackingRecord(@PathVariable(value="f") String flightNumber) {
        if (Flight.isFlightNumber(flightNumber)) {
            TrackingRecord t = trackingRecordService.latestRecord(flightNumber);
            if (t != null)
                return t;
        }
        return TrackingRecord.emptyRecord();
    }

    @GetMapping("/api/{f}/history")
    public List<TrackingRecord> history(@PathVariable(value="f") String flightNumber,
            @RequestParam(value="p", defaultValue = "0") String page,
            @RequestParam(value="n", defaultValue = "5") String amount) {
        if (Flight.isFlightNumber(flightNumber)) {
            int p, n;
            try {
                p = Integer.parseInt(page.replace('-', '?'));
            } catch (NumberFormatException e) {
                p = 0;
            }
            try {
                n = Integer.parseInt(amount.replace('-', '?'));
            } catch (NumberFormatException e) {
                n = 5;
            }
            List<TrackingRecord> records = trackingRecordService.trackingHistory(flightNumber, p, n);
            if (!records.isEmpty())
                return records;
        }
        List<TrackingRecord> empty = new ArrayList<TrackingRecord>(2);
        empty.add(TrackingRecord.emptyRecord());
        return empty;
    }
}
