package adsbrecorder.dataintlv.task;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.repo.TrackingRecordRepository;

@Component
public class CallsignUpdateTask {

    private TrackingRecordRepository trackingRecordRepository;

    @Autowired
    public CallsignUpdateTask(TrackingRecordRepository trackingRecordRepository) {
        this.trackingRecordRepository = requireNonNull(trackingRecordRepository);
    }

    @Scheduled(fixedDelay = 10000)
    public void updateNullCallsigns() {
        List<TrackingRecord> nullCallsigns = trackingRecordRepository.findAllNullCallsign();
        nullCallsigns.forEach(tr -> tr.setFlight(
                trackingRecordRepository.findCallsign(tr.getAddressICAO(), tr.getLastTimeSeen())));
        trackingRecordRepository.saveAll(nullCallsigns);
    }
}
