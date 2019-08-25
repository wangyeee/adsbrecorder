package adsbrecorder.receiver.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.repo.RemoteReceiverRepository;
import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.entity.VelocityUpdate;
import adsbrecorder.receiver.repo.TrackingRecordRepository;
import adsbrecorder.receiver.repo.VelocityUpdateRepository;
import adsbrecorder.receiver.service.VelocityUpdateService;

@Service
public class VelocityUpdateServiceImpl implements VelocityUpdateService {

    private VelocityUpdateRepository velocityUpdateRepository;
    private RemoteReceiverRepository remoteReceiverRepository;
    private TrackingRecordRepository trackingRecordRepository;

    @Value("${adsbrecorder.data.date_range_expn:600000}")
    private long dateRangeExpansion;

    @Autowired
    public VelocityUpdateServiceImpl(VelocityUpdateRepository velocityUpdateRepository,
            RemoteReceiverRepository remoteReceiverRepository,
            TrackingRecordRepository trackingRecordRepository) {
        this.velocityUpdateRepository = requireNonNull(velocityUpdateRepository);
        this.remoteReceiverRepository = requireNonNull(remoteReceiverRepository);
        this.trackingRecordRepository = requireNonNull(trackingRecordRepository);
    }

    @Override
    public VelocityUpdate addVelocityUpdate(VelocityUpdate update) {
        return velocityUpdateRepository.save(update);
    }

    @Override
    public List<VelocityUpdate> batchCreateVelocityUpdates(Collection<VelocityUpdate> updates,
            String sourceReceiverName,
            String sourceReceiverKey) {
        Optional<RemoteReceiver> sourceReceiver = remoteReceiverRepository.findOneByRemoteReceiverNameAndKey(sourceReceiverName, sourceReceiverKey);
        if (sourceReceiver.isPresent()) {
            updates.forEach(update -> update.setSourceReceiver(sourceReceiver.get()));
            return velocityUpdateRepository.saveAll(updates);
        }
        return List.of();
    }

    @Override
    @Deprecated(forRemoval = true)
    public void interleavingTrackingRecords(int addressICAO, long lastTime) {
        List<VelocityUpdate> vus = velocityUpdateRepository.findAllActiveByAddressICAO(addressICAO, lastTime);
        long startTime = vus.get(vus.size() - 1).getLastTimeSeen() - this.dateRangeExpansion;
        long endTime = vus.get(0).getLastTimeSeen() + this.dateRangeExpansion;
        List<TrackingRecord> trs = trackingRecordRepository.llfindAllByICAOAddressAndLastSeenBetween(addressICAO, startTime, endTime);
        interleave(vus, trs);
        velocityUpdateRepository.saveAll(vus);
        trackingRecordRepository.saveAll(trs);
    }

    private void interleave(List<VelocityUpdate> vus, List<TrackingRecord> trs) {
        List<Object> sort = new ArrayList<Object>(vus.size() + trs.size());
        ConstantAccelerationInterleaving0 cai = new ConstantAccelerationInterleaving0(vus, trs);
        int i, j;
        for (i = 0, j = 0; i < vus.size() && j < trs.size();) {
            VelocityUpdate vu = vus.get(i);
            TrackingRecord tr = trs.get(j);
            if (vu.getLastTimeSeen() > tr.getLastTimeSeen()) {
                sort.add(vu);
                cai.updateVelocity(i);
                vu.setApplied(true);
                i++;
            } else {
                sort.add(tr);
                cai.addTrackingRecord(j);
                j++;
            }
        }
        for (;i < vus.size();i++) {
            vus.get(i).setApplied(true);
            sort.add(vus.get(i));
        }
        VelocityUpdate end = vus.get(vus.size() - 1);
        for (;j < trs.size();j++) {
            TrackingRecord tr = trs.get(j);
            if (tr.getVelocity() == 0) {
                tr.setVelocity(end.getVelocity());
                tr.setHeading(end.getHeading());
                tr.setVerticalRate(end.getVerticalRate());
            }
            sort.add(tr);
        }
    }
}

@Deprecated(forRemoval = true)
class ConstantAccelerationInterleaving0 {
    private List<VelocityUpdate> vus;
    private List<TrackingRecord> trs;
    private List<TrackingRecord> wu;
    private int velStart, velEnd, skipped;
    private boolean trReady;

    ConstantAccelerationInterleaving0(List<VelocityUpdate> vus, List<TrackingRecord> trs) {
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
                    tr.setVelocity(start.getVelocity());
                    tr.setHeading(start.getHeading());
                    tr.setVerticalRate(start.getVerticalRate());
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
                tr.setVelocity((int) (startV - i * deltaV));
                tr.setVerticalRate((int) (startVH - i * deltaVH));
                tr.setHeading((int)(startHdr - i * deltaHdr));
            }
        }
    }
}
