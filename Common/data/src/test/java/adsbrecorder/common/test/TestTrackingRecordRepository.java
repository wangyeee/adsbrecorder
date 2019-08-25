package adsbrecorder.common.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import adsbrecorder.common.test.conf.MongoDBTestConfiguration;
import adsbrecorder.receiver.repo.TrackingRecordRepository;

@EnableMongoRepositories(basePackages = {"adsbrecorder.receiver.repo"})
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = MongoDBTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestTrackingRecordRepository {

    @Autowired
    TrackingRecordRepository trackingRecordRepository;

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
}
