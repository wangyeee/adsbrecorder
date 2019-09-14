package adsbrecorder.receiver.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import adsbrecorder.receiver.entity.TrackingRecord;

public class TrackingRecordSerializer implements Serializer<TrackingRecord> {

    public TrackingRecordSerializer() {
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, TrackingRecord data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception e) {
            System.err.println("TrackingRecordSerializer error: " + e.getMessage());
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public void close() {
    }
}
