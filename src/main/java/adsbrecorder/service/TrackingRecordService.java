package adsbrecorder.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import adsbrecorder.entity.TrackingRecord;

public interface TrackingRecordService {

    void addRecord(TrackingRecord record);
    TrackingRecord latestRecord(String flightNumber);
    List<TrackingRecord> trackingHistory(String flightNumber, int page, int amount);
    Map<String, TrackingRecord> getLiveRecords();
    List<TrackingRecord> getLiveTrack(String flightNumber);
    List<TrackingRecord> getTrackBetween(String flightNumber, Date after, Date before);
    List<TrackingRecord> getTrackOn(String flightNumber, Date date);
    List<TrackingRecord> getTrackBefore(String flightNumber, Date before);
    List<TrackingRecord> getTrackAfter(String flightNumber, Date after);
    List<Date> findDatesWithFlight(String flightNumber, int page, int amount);
    void setLocalReceiver(boolean receiver);
    boolean hasLocalReceiver();
}
