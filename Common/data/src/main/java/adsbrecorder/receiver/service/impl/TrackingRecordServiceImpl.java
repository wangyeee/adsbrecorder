package adsbrecorder.receiver.service.impl;

import static java.util.Objects.requireNonNull;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.repo.RemoteReceiverRepository;
import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.repo.TrackingRecordDataRepository;
import adsbrecorder.receiver.repo.TrackingRecordRepository;
import adsbrecorder.receiver.service.TrackingRecordService;

@Service
public class TrackingRecordServiceImpl implements TrackingRecordService {

    private TrackingRecordRepository trackingRecordRepository;
    private RemoteReceiverRepository remoteReceiverRepository;
    private TrackingRecordDataRepository trackingRecordDataRepository;

    @Autowired
    public TrackingRecordServiceImpl(TrackingRecordRepository trackingRecordRepository,
            RemoteReceiverRepository remoteReceiverRepository,
            TrackingRecordDataRepository trackingRecordDataRepository) {
        this.trackingRecordRepository = requireNonNull(trackingRecordRepository);
        this.remoteReceiverRepository = requireNonNull(remoteReceiverRepository);
        this.trackingRecordDataRepository = requireNonNull(trackingRecordDataRepository);
    }

    @Override
    public Map<String, Date> getRecentFlights(int amount) {
        return trackingRecordDataRepository.recentFlights(amount);
    }

    @Override
    public List<TrackingRecord> batchCreateTrackingRecord(Collection<TrackingRecord> records,
            String sourceReceiverName,
            String sourceReceiverKey) {
        Optional<RemoteReceiver> sourceReceiver = remoteReceiverRepository.findOneByRemoteReceiverNameAndKey(sourceReceiverName, sourceReceiverKey);
        if (sourceReceiver.isPresent()) {
            records.forEach(record -> record.setSourceReceiver(sourceReceiver.get()));
            return trackingRecordRepository.saveAll(records);
        }
        return List.of();
    }

    @Override
    public TrackingRecord findById(BigInteger id) {
        Optional<TrackingRecord> tr = trackingRecordRepository.findById(id);
        return tr.isPresent() ? tr.get() : null;
    }

    @Override
    public List<TrackingRecord> findAllByFlightNumber(String flight, Date startDate, Date endDate) {
        if (startDate == null)
            return endDate == null ? trackingRecordRepository.findAllByFlight(flight) :
                trackingRecordRepository.findAllByFlightAndRecordDateLessThan(flight, endDate);
        return endDate == null ? trackingRecordRepository.findAllByFlightAndRecordDateGreaterThan(flight, startDate):
            trackingRecordRepository.findAllByFlightAndRecordDateBetween(flight, startDate, endDate);
    }

    @Override
    public List<TrackingRecord> findAllByFlightNumber(String flight, long lastSeenStart, long lastSeenEnd) {
        return trackingRecordRepository.findAllByFlightAndLastTimeSeenBetween(flight, lastSeenStart, lastSeenEnd);
    }
}
