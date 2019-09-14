package adsbrecorder.receiver.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import adsbrecorder.receiver.entity.TrackingRecord;

public class TrackingRecordDeserializer implements Deserializer<TrackingRecord> {

    public TrackingRecordDeserializer() {
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public TrackingRecord deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, TrackingRecord.class);
        } catch (Exception e) {
            System.err.println("TrackingRecordDeserializer error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
    }
}
