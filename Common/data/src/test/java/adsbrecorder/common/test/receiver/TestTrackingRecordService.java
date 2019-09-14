package adsbrecorder.common.test.receiver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.service.RemoteReceiverService;
import adsbrecorder.common.test.conf.EmbeddedMongoDBTestConfiguration;
import adsbrecorder.receiver.entity.TrackingRecord;
import adsbrecorder.receiver.service.TrackingRecordService;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@EnableMongoRepositories(basePackages = {
        "adsbrecorder.receiver.repo"})
@EnableJpaRepositories(basePackages = {
        "adsbrecorder.client.repo",
        "adsbrecorder.user.repo"})
@ComponentScan(basePackages = {
        "adsbrecorder.receiver.repo",
        "adsbrecorder.client.service",
        "adsbrecorder.user.service",
        "adsbrecorder.receiver.service"})
@EntityScan(basePackages = {
        "adsbrecorder.client.entity",
        "adsbrecorder.user.entity"})
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = EmbeddedMongoDBTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
public class TestTrackingRecordService {

    private static final long ONE_DAY = 1000 * 3600 * 24L;
    private static boolean dataLoaded = false;
    private final static Map<Integer, String> icaoCallsigns = Map.of(
        1, "ABC1",
        2, "ABC2",
        3, "ABC3",
        4, "ABC4",
        5, "ABC5"
    );

    private TrackingRecordService trackingRecordService;
    private UserService userService;
    private RemoteReceiverService remoteReceiverService;
    private Random random;
    private final String dummyReceiverName = "TestReceiver";

    @Autowired
    public TestTrackingRecordService(UserService userService,
            TrackingRecordService trackingRecordService,
            RemoteReceiverService remoteReceiverService) {
        this.userService = Objects.requireNonNull(userService);
        this.trackingRecordService = Objects.requireNonNull(trackingRecordService);
        this.remoteReceiverService = Objects.requireNonNull(remoteReceiverService);
        this.random = new Random();
    }

    @PostConstruct
    public void loadTestData() {
        if (dataLoaded) return;
        User user = userService.createNewUser("TestUser", "TestUser password");
        RemoteReceiver receiver = remoteReceiverService.createRemoteReceiver(dummyReceiverName, "TestReceiver description", user);
        final int recordCount = 10;
        icaoCallsigns.forEach((icao, callsign) -> {
            List<TrackingRecord> records = IntStream.range(0, recordCount)
                    .mapToObj(i -> testDataTrackingRecord(icao, callsign))
                    .collect(Collectors.toList());
            trackingRecordService.batchCreateTrackingRecord(records, dummyReceiverName, receiver.getRemoteReceiverKey());
        });
        dataLoaded = true;
    }

    @Test
    public void testGetRecentFlights() {
        final int amount = 5;
        Map<String, Date> recentFlights = trackingRecordService.getRecentFlights(amount);
        assertFalse(recentFlights.isEmpty());
    }

    @Test
    public void testBatchCreateTrackingRecord() {
        final RemoteReceiver receiver = remoteReceiverService.findRemoteReceiver(dummyReceiverName);
        final int recordCount = 10;
        icaoCallsigns.forEach((icao, callsign) -> {
            List<TrackingRecord> records = IntStream.range(0, recordCount)
                    .mapToObj(i -> testDataTrackingRecord(icao, callsign))
                    .collect(Collectors.toList());
            List<TrackingRecord> recordsCreated = trackingRecordService.batchCreateTrackingRecord(records,
                    receiver.getRemoteReceiverName(), receiver.getRemoteReceiverKey());
            assertEquals(records.size(), recordsCreated.size());
        });
    }

    @Test
    public void testFindById() {
        BigInteger notExists = BigInteger.valueOf(-1L);
        TrackingRecord record = trackingRecordService.findById(notExists);
        assertNull(record);
        final RemoteReceiver receiver = remoteReceiverService.findRemoteReceiver(dummyReceiverName);
        icaoCallsigns.forEach((icao, callsign) -> {
            TrackingRecord newRecord = testDataTrackingRecord(icao, callsign);
            newRecord = trackingRecordService.batchCreateTrackingRecord(List.of(newRecord),
                    receiver.getRemoteReceiverName(), receiver.getRemoteReceiverKey()).get(0);
            TrackingRecord findById = trackingRecordService.findById(newRecord.getId());
            assertEquals(newRecord, findById);
        });
    }

    @Test
    public void testFindAllByFlightNumberDate() {
        final long now = System.currentTimeMillis();
        final long start = now - ONE_DAY;
        icaoCallsigns.values().forEach(callsign -> {
            List<TrackingRecord> records = trackingRecordService.findAllByFlightNumber(callsign,
                    new Date(start), new Date(now));
            records.forEach(record -> {
                assertTrue(record.getRecordDate().getTime() > start && record.getRecordDate().getTime() < now);
            });
            assertTrue(records.size() > 0);
            records = trackingRecordService.findAllByFlightNumber(callsign,
                    new Date(start), null);
            records.forEach(record -> {
                assertTrue(record.getRecordDate().getTime() > start);
            });
            records = trackingRecordService.findAllByFlightNumber(callsign,
                    null, new Date(now));
            records.forEach(record -> {
                assertTrue(record.getRecordDate().getTime() < now);
            });
            records = trackingRecordService.findAllByFlightNumber(callsign,
                    null, null);
            assertTrue(records.size() > 0);
        });
    }

    @Test
    public void testFindAllByFlightNumberLong() {
        final long now = System.currentTimeMillis();
        final long start = now - ONE_DAY;
        icaoCallsigns.values().forEach(callsign -> {
            List<TrackingRecord> records = trackingRecordService.findAllByFlightNumber(callsign, start, now);
            assertTrue(records.size() > 0);
            records.forEach(record -> {
                assertTrue(record.getLastTimeSeen() > start && record.getLastTimeSeen() < now);
            });
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
