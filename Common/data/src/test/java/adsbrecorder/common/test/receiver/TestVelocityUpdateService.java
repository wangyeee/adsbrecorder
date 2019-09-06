package adsbrecorder.common.test.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.List;
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
import adsbrecorder.common.test.conf.InMemoryDBTestConfiguration;
import adsbrecorder.receiver.entity.VelocityUpdate;
import adsbrecorder.receiver.service.VelocityUpdateService;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@EnableMongoRepositories(basePackages = {"adsbrecorder.receiver.repo"})
@EnableJpaRepositories(basePackages = {
        "adsbrecorder.client.repo",
        "adsbrecorder.user.repo"})
@ComponentScan(basePackages = {
        "adsbrecorder.client",
        "adsbrecorder.client.service",
        "adsbrecorder.user.service",
        "adsbrecorder.receiver.service",
        "adsbrecorder.receiver.repo",
        "adsbrecorder.client.controller",
        "adsbrecorder.client.auth"})
@EntityScan(basePackages = {
        "adsbrecorder.client.entity",
        "adsbrecorder.user.entity"})
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = {
        EmbeddedMongoDBTestConfiguration.class,
        InMemoryDBTestConfiguration.class})
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest
public class TestVelocityUpdateService {

    private VelocityUpdateService velocityUpdateService;
    private UserService userService;
    private RemoteReceiverService remoteReceiverService;

    private Random random;
    private final String dummyReceiverName = "TestReceiver";

    private static boolean testDataCreated = false;

    @Autowired
    public TestVelocityUpdateService(UserService userService,
            VelocityUpdateService velocityUpdateService,
            RemoteReceiverService remoteReceiverService) {
        this.userService = Objects.requireNonNull(userService);
        this.velocityUpdateService = Objects.requireNonNull(velocityUpdateService);
        this.remoteReceiverService = Objects.requireNonNull(remoteReceiverService);
        this.random = new Random();
    }

    @PostConstruct
    public void createTestUserAndReceiver() {
        if (testDataCreated) return;
        User user = userService.createNewUser("TestUserVU", "TestUserVU password");
        remoteReceiverService.createRemoteReceiver(dummyReceiverName, "TestReceiver description", user);
        testDataCreated = true;
    }

    @Test
    public void testBatchCreateVelocityUpdates() {
        final int size = 5;
        final int icaoTest = 54321;
        RemoteReceiver receiver = this.remoteReceiverService.findRemoteReceiver(dummyReceiverName);
        List<VelocityUpdate> vus = IntStream.range(0, size)
            .mapToObj(i -> randomVelocityUpdate(icaoTest, receiver))
            .collect(Collectors.toList());
        List<VelocityUpdate> vusSaved = velocityUpdateService.batchCreateVelocityUpdates(vus,
                receiver.getRemoteReceiverName(),
                receiver.getRemoteReceiverKey());
        assertEquals(vus.size(), vusSaved.size());
    }

    @Test
    public void testAddVelocityUpdate() {
        final int icaoTest = 12345;
        RemoteReceiver receiver = this.remoteReceiverService.findRemoteReceiver(dummyReceiverName);
        VelocityUpdate update = randomVelocityUpdate(icaoTest, receiver);
        assertNull(update.getId());
        VelocityUpdate savedVelocityUpdate = velocityUpdateService.addVelocityUpdate(update);
        assertNotNull(savedVelocityUpdate.getId());
    }

    private VelocityUpdate randomVelocityUpdate(int icao, RemoteReceiver source) {
        VelocityUpdate update = new VelocityUpdate();
        update.setAddressICAO(icao);
        update.setApplied(false);
        update.setHeading(random.nextInt(360));
        update.setLastTimeSeen(System.currentTimeMillis());
        update.setRecordDate(new Date());
        update.setVelocity(random.nextInt(500));
        update.setSourceReceiver(source);
        update.setVerticalRate(random.nextInt(1000) - 500);
        return update;
    }
}
