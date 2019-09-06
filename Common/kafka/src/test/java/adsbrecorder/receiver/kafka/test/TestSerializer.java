package adsbrecorder.receiver.kafka.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;

import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.kafka.TrackingRecordDeserializer;
import adsbrecorder.receiver.kafka.TrackingRecordSerializer;

public class TestSerializer {

    private Random random = new Random();

    @Test
    public void testSerializerDeSerializer() {
        final TrackingRecordSerializer serializer = new TrackingRecordSerializer();
        final TrackingRecordDeserializer deserializer = new TrackingRecordDeserializer();
        final int amount = 10;
        IntStream.range(0, amount).forEach(i -> {
            final int icao = Math.abs(random.nextInt()) + i;
            final String callsign = RandomStringUtils.randomAlphabetic(6);
            TrackingRecord record = testDataTrackingRecord(icao, callsign);
            byte[] data = serializer.serialize(null, record);
            TrackingRecord restore = deserializer.deserialize(null, data);
            assertEquals(record, restore);
        });
        serializer.close();
        deserializer.close();
    }

    private TrackingRecord testDataTrackingRecord(int icao, String callsign) {
        TrackingRecord tr = new TrackingRecord();
        tr.setAddressICAO(icao);
        tr.setFlight(callsign);
        tr.setId(BigInteger.valueOf(random.nextLong()));
        tr.setAltitude(random.nextInt(12000));
        tr.setHeading(random.nextInt(360));
        tr.setVelocity(random.nextInt(500));
        tr.setVerticalRate(random.nextInt(1000) - 500);
        tr.setLatitude(random.nextDouble());
        tr.setLongitude(random.nextDouble());
        long date = System.currentTimeMillis() - 1000 * 3600 * (random.nextInt(24) + 1);
        tr.setLastTimeSeen(date);
        tr.setRecordDate(new Date(date));
        return tr;
    }
}
