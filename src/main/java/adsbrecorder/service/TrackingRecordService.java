package adsbrecorder.service;

import java.util.List;
import java.util.Map;

import adsbrecorder.entity.TrackingRecord;

public interface TrackingRecordService {

    void addRecord(TrackingRecord record);
    TrackingRecord latestRecord(String flightNumber);
    List<TrackingRecord> trackingHistory(String flightNumber, int page, int amount);
    Map<String, TrackingRecord> getLiveRecords();
    List<TrackingRecord> getLiveTrack(String flightNumber);
    void setLocalReceiver(boolean receiver);
    boolean hasLocalReceiver();
}
