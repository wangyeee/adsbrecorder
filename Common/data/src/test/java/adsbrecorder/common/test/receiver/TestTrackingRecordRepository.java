package adsbrecorder.common.test.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import adsbrecorder.common.test.conf.EmbeddedMongoDBTestConfiguration;
import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.repo.TrackingRecordRepository;

@EnableMongoRepositories(basePackages = {"adsbrecorder.receiver.repo"})
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = EmbeddedMongoDBTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestTrackingRecordRepository {

    private TrackingRecordRepository trackingRecordRepository;
    private Random random;

    @Autowired
    public TestTrackingRecordRepository(TrackingRecordRepository trackingRecordRepository) {
        this.trackingRecordRepository = Objects.requireNonNull(trackingRecordRepository);
        this.random = new Random();
    }

    @PostConstruct
    public void initTestData() {
        List<TrackingRecord> testData = List.of(
                testDataTrackingRecord(8132501, "TMN1"),
                testDataTrackingRecord(9015532, "CAL054"),
                testDataTrackingRecord(13117453, "PLC1"));
        final int size0 = testData.size();
        assertEquals(size0, trackingRecordRepository.saveAll(testData).size());
    }

    @Test
    public void testFindCallsign() {
        final String fmt = "Checking callsign for ICAO address: %06X, expected: %s, actual: %s";
        Map<Integer, String> icaoCallsigns = Map.of(
            8132501, "TMN1",
            9015532, "CAL054",
            13117453, "PLC1",
            -1, "No Callsign"
        );
        icaoCallsigns.forEach((icao, callsign) -> {
            String actual = trackingRecordRepository.findCallsign(icao);
            System.err.println(String.format(fmt, icao, callsign, actual));
            assertEquals(callsign, actual);
        });
    }

    private TrackingRecord testDataTrackingRecord(int icao, String callsign) {
        TrackingRecord tr = new TrackingRecord();
        tr.setAddressICAO(icao);
        tr.setFlight(callsign);
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
