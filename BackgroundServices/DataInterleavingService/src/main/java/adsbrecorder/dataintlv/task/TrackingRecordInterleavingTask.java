package adsbrecorder.dataintlv.task;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.entity.VelocityUpdate;
import adsbrecorder.receiver.kafka.ListOfTopics;
import adsbrecorder.receiver.repo.TrackingRecordRepository;
import adsbrecorder.receiver.repo.VelocityUpdateRepository;

@Component
public class TrackingRecordInterleavingTask implements ListOfTopics {

    private VelocityUpdateRepository velocityUpdateRepository;
    private TrackingRecordRepository trackingRecordRepository;

    @Value("${adsbrecorder.data.date_range_expn:600000}")
    private long dateRangeExpansion;

    @Autowired
    public TrackingRecordInterleavingTask(VelocityUpdateRepository velocityUpdateRepository,
            TrackingRecordRepository trackingRecordRepository) {
        this.velocityUpdateRepository = requireNonNull(velocityUpdateRepository);
        this.trackingRecordRepository = requireNonNull(trackingRecordRepository);
    }

    @KafkaListener(topics = INTERLEAVING_RECORDS, groupId = "1")
    public void interleaveRecords(TrackingRecord record) {
        record = requireNonNull(record);
        System.err.println("Try to interleave: " + record);
        List<VelocityUpdate> vus = velocityUpdateRepository.findAllActiveByAddressICAO(record.getAddressICAO(), record.getLastTimeSeen());
        if (vus.size() > 1) {
            long startTime = vus.get(vus.size() - 1).getLastTimeSeen() - this.dateRangeExpansion;
            long endTime = vus.get(0).getLastTimeSeen() + this.dateRangeExpansion;
            List<TrackingRecord> trs = trackingRecordRepository.llfindAllByICAOAddressAndLastSeenBetween(record.getAddressICAO(), startTime, endTime);
            interleave(vus, trs);
            velocityUpdateRepository.saveAll(vus);
            trackingRecordRepository.saveAll(trs);
        } else {
            System.err.println("Not enough velocity data: " + vus.size());
        }
    }

    private void interleave(List<VelocityUpdate> vus, List<TrackingRecord> trs) {
        ConstantAccelerationInterleaving cai = new ConstantAccelerationInterleaving(vus, trs);
        int i, j;
        for (i = 0, j = 0; i < vus.size() && j < trs.size();) {
            VelocityUpdate vu = vus.get(i);
            TrackingRecord tr = trs.get(j);
            if (vu.getLastTimeSeen() > tr.getLastTimeSeen()) {
                cai.updateVelocity(i);
                vu.setApplied(true);
                i++;
            } else {
                cai.addTrackingRecord(j);
                j++;
            }
        }
        for (;i < vus.size();i++) {
            vus.get(i).setApplied(true);
        }
        VelocityUpdate end = vus.get(vus.size() - 1);
        for (;j < trs.size();j++) {
            TrackingRecord tr = trs.get(j);
            if (tr.getVelocity() == 0) {
                StringBuilder sb = new StringBuilder("[TrackingRecord]");  // TODO remove
                sb.append(String.format("(%s), ICAO=%d, ", tr.getId().toString(), tr.getAddressICAO()));
                sb.append(ConstantAccelerationInterleaving.dumpTR(tr));
                sb.append(" -> ");
                tr.setVelocity(end.getVelocity());
                tr.setHeading(end.getHeading());
                tr.setVerticalRate(end.getVerticalRate());
                sb.append(ConstantAccelerationInterleaving.dumpTR(tr));
                System.err.println(sb.toString());
            }
        }
    }
}

class ConstantAccelerationInterleaving {
    private List<VelocityUpdate> vus;
    private List<TrackingRecord> trs;
    private List<TrackingRecord> wu;
    private int velStart, velEnd, skipped;
    private boolean trReady;

    ConstantAccelerationInterleaving(List<VelocityUpdate> vus, List<TrackingRecord> trs) {
        this.vus = vus;
        this.trs = trs;
        velStart = -1;
        skipped = -1;
        trReady = false;
        wu = new ArrayList<TrackingRecord>();
    }

    void addTrackingRecord(int index) {
        if (velStart  == -1) {
            skipped = index;
        } else {
            trReady = true;
            wu.add(trs.get(index));
        }
        if (skipped > -1 && trReady == true) {
            int i = 0;
            VelocityUpdate start = vus.get(velStart);
            do {
                TrackingRecord tr = trs.get(i);
                if (tr.getVelocity() == 0) {
                    StringBuilder sb = new StringBuilder("[TrackingRecord]");  // TODO remove
                    sb.append(String.format("(%s), ICAO=%d, ", tr.getId().toString(), tr.getAddressICAO()));
                    sb.append(dumpTR(tr));
                    sb.append(" -> ");
                    tr.setVelocity(start.getVelocity());
                    tr.setHeading(start.getHeading());
                    tr.setVerticalRate(start.getVerticalRate());
                    sb.append(dumpTR(tr));
                    System.err.println(sb.toString());
                }
                i++;
            } while (i <= skipped);
            skipped = -1;
        }
    }

    void updateVelocity(int index) {
        if (velStart == -1) {
            velStart = index;
        } else if (trReady) {
            velEnd = index;
            interleaveVel(velStart, velEnd);
            wu.clear();
            velStart = index;
        } else {
            velStart = index;
        }
    }

    private void interleaveVel(int start, int end) {
        double startV = vus.get(start).getVelocity();
        double deltaV = startV - vus.get(end).getVelocity();
        deltaV /= wu.size() + 1;
        double startVH = vus.get(start).getVerticalRate();
        double deltaVH = startVH - vus.get(end).getVerticalRate();
        deltaVH /= wu.size() + 1;
        double startHdr = vus.get(start).getHeading();
        double deltaHdr = startHdr - vus.get(end).getHeading();
        deltaHdr /= wu.size() + 1;
        for (int i = 0; i < wu.size();) {
            TrackingRecord tr = wu.get(i);
            i++;
            if (tr.getVelocity() == 0) {
                StringBuilder sb = new StringBuilder("[TrackingRecord]");  // TODO remove
                sb.append(String.format("(%s), ICAO=%d, ", tr.getId().toString(), tr.getAddressICAO()));
                sb.append(dumpTR(tr));
                sb.append(" -> ");
                tr.setVelocity((int) (startV - i * deltaV));
                tr.setVerticalRate((int) (startVH - i * deltaVH));
                tr.setHeading((int)(startHdr - i * deltaHdr));
                sb.append(dumpTR(tr));
                System.err.println(sb.toString());
            }
        }
    }

    static String dumpTR(TrackingRecord tr) {
        return String.format("vel=%d, velH=%d, hdr=%d", tr.getVelocity(), tr.getVerticalRate(), tr.getHeading());
    }
}
