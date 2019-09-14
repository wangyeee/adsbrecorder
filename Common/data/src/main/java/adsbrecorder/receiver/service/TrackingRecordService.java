package adsbrecorder.receiver.service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import adsbrecorder.receiver.entity.TrackingRecord;

public interface TrackingRecordService {
    List<TrackingRecord> batchCreateTrackingRecord(Collection<TrackingRecord> records, String sourceReceiverName, String sourceReceiverKey);
    TrackingRecord findById(BigInteger id);
    List<TrackingRecord> findAllByFlightNumber(String flight, Date startDate, Date endDate);
    List<TrackingRecord> findAllByFlightNumber(String flight, long lastSeenStart, long lastSeenEnd);
    Map<String, Date> getRecentFlights(int amount);
}
