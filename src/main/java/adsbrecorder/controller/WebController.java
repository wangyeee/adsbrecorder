package adsbrecorder.controller;

import static adsbrecorder.entity.Flight.isFlightNumber;
import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import adsbrecorder.entity.TrackingRecord;
import adsbrecorder.service.TrackingRecordService;

@Controller
public class WebController {

    private TrackingRecordService trackingRecordService;

    @Autowired
    public WebController(TrackingRecordService trackingRecordService) {
        this.trackingRecordService = requireNonNull(trackingRecordService);
    }

    @GetMapping(value = {"/", "/live"})
    public String index(Model model) {
        model.addAttribute("currtime", new Date());
        //model.addAttribute("islive", Boolean.toString(trackingRecordService.hasLocalReceiver()));
        //if (!trackingRecordService.hasLocalReceiver()) {
        //}
        model.addAttribute("dates", trackingRecordService.findDatesWithAnyFlight(0, 10));
        return "index";
    }

    @GetMapping(value = "/map")
    public String map(@RequestParam(value="f") String flightNumber,
            @RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date, Model model) {
        if (isFlightNumber(flightNumber)) {
            model.addAttribute("flightNumber", flightNumber);
            model.addAttribute("flightDate", date);
            List<TrackingRecord> records = trackingRecordService.getTrackOn(flightNumber, date);
            if (records.isEmpty()) {
                model.addAttribute("cenlati", 0);
                model.addAttribute("cenlong", 0);
            } else {
                double cenlati = 0.0;
                double cenlong = 0.0;
                int i = 0;
                for (TrackingRecord record : records) {
                    if (record.getLatitude() != 0 && record.getLongitude() != 0) {
                        cenlati += record.getLatitude();
                        cenlong += record.getLongitude();
                        i++;
                    }
                }
                model.addAttribute("cenlati", cenlati / i);
                model.addAttribute("cenlong", cenlong / i);
            }
        }
        return "map";
    }

    @GetMapping(value = "/air")
    public String airline() {
        return "airline";
    }
}
