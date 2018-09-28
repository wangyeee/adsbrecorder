package adsbrecorder.controller;

import static adsbrecorder.entity.Flight.isFlightNumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.entity.TrackingRecord;
import adsbrecorder.service.TrackingRecordService;

@RestController
public class RealtimeController {

    private TrackingRecordService trackingRecordService;

    @Autowired
    public RealtimeController(TrackingRecordService trackingRecordService) {
        this.trackingRecordService = Objects.requireNonNull(trackingRecordService);
    }

    @GetMapping("/api/track")
    public List<TrackingRecord> track(@RequestParam(value="f") String flightNumber) {
        if (isFlightNumber(flightNumber))
            return trackingRecordService.getLiveTrack(flightNumber);
        return Collections.emptyList();
    }

    @GetMapping("/api/live")
    public List<TrackingRecord> liveData(@RequestParam(value="n", defaultValue = "0") String maxAmountStr) {
        int maxAmount;
        try {
            maxAmount = Integer.parseInt(maxAmountStr);
        } catch (NumberFormatException e) {
            maxAmount = 0;
        }
        Collection<TrackingRecord> live = trackingRecordService.getLiveRecords().values();
        if (maxAmount > 0) {
            List<TrackingRecord> trs = new ArrayList<>(maxAmount);
            for (TrackingRecord t : live) {
                if (maxAmount > 0) {
                    trs.add(t);
                    maxAmount--;
                } else {
                    break;
                }
            }
            return trs;
        }
        return new ArrayList<TrackingRecord>(live);
    }
}
