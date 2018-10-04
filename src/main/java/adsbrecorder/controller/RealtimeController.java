package adsbrecorder.controller;

import static adsbrecorder.entity.Flight.isFlightNumber;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.entity.TrackingRecord;
import adsbrecorder.service.TrackingRecordService;

@RestController
public class RealtimeController {

    private TrackingRecordService trackingRecordService;

    @Autowired
    public RealtimeController(TrackingRecordService trackingRecordService) {
        this.trackingRecordService = requireNonNull(trackingRecordService);
    }

    @GetMapping("/api/{f}/live")
    public List<TrackingRecord> track(@PathVariable(value="f") String flightNumber) {
        if (isFlightNumber(flightNumber))
            return trackingRecordService.getLiveTrack(flightNumber);
        return Collections.emptyList();
    }

    @GetMapping("/api/{f}/found")
    public List<Date> findDatesWithFlight(@PathVariable(value="f") String flightNumber,
            @RequestParam(value="p", defaultValue = "0") String pageStr,
            @RequestParam(value="n", defaultValue = "5") String amountStr) {
        if (isFlightNumber(flightNumber)) {
            int page, amount;
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 0;
            }
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                amount = 0;
            }
            return trackingRecordService.findDatesWithFlight(flightNumber, page, amount);
        }
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
