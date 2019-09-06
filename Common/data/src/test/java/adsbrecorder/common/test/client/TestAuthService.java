package adsbrecorder.common.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import adsbrecorder.client.entity.RemoteReceiver;
import adsbrecorder.client.service.AuthService;
import adsbrecorder.client.service.RemoteReceiverService;
import adsbrecorder.common.test.conf.InMemoryDBTestConfiguration;
import adsbrecorder.user.service.UserService;


@EnableJpaRepositories(basePackages = {
        "adsbrecorder.user.repo",
        "adsbrecorder.client.repo"})
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = InMemoryDBTestConfiguration.class)
@ComponentScan(basePackages = {
        "adsbrecorder.client",
        "adsbrecorder.client.service",
        "adsbrecorder.user.service"})
@EntityScan(basePackages = {
        "adsbrecorder.user.entity",
        "adsbrecorder.client.entity"})
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestAuthService {

    @Autowired
    public RemoteReceiverService remoteReceiverService;

    @Autowired
    public UserService userService;

    @Autowired
    private AuthService authService;

    private final String testReceiverOwner = "TestReceiverOwner";
    private final String testReceiver = "TestReceiver";

    private static boolean testDataCreated = false;

    @PostConstruct
    public void createTestUserAndReceiver() {
        if (testDataCreated) return;
        remoteReceiverService.createRemoteReceiver(testReceiver, "TestReceiver description",
                userService.createNewUser(testReceiverOwner, "TestReceiverOwner Password"));
        testDataCreated = true;
    }

    @Test
    public void testAuthenticate() {
        RemoteReceiver receiver = remoteReceiverService.findRemoteReceiver(testReceiver);
        RemoteReceiver authorized = authService.authenticate(testReceiver, receiver.getRemoteReceiverKey());
        assertEquals(receiver, authorized);
        RemoteReceiver unauthorized = authService.authenticate(testReceiver, receiver.getRemoteReceiverKey() + "_incorrect");
        assertNotEquals(receiver, unauthorized);
    }
}
