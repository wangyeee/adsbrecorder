package adsbrecorder.common.test.client;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import adsbrecorder.client.service.RemoteReceiverService;
import adsbrecorder.common.test.conf.InMemoryDBTestConfiguration;
import adsbrecorder.user.entity.User;
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
public class TestRemoteReceiverService {

    @Autowired
    public RemoteReceiverService remoteReceiverService;

    @Autowired
    public UserService userService;

    private final String userWReceivers = "UserWithReceivers";
    private final String userWOReceivers = "UserWithOutReceivers";

    private static boolean testUsersCreated = false;

    @PostConstruct
    public void createTestUsers() {
        if (testUsersCreated) return;
        userService.createNewUser(userWReceivers, "UserWithReceivers Password");
        userService.createNewUser(userWOReceivers, "UserWithOutReceivers Password");
        testUsersCreated = true;
    }

    @Test
    public void testCreateRemoteReceiver() {
        final String name = "TestCreateRemoteReceiver";
        final String description = "TestCreateRemoteReceiver description";
        User owner = userService.findUserByName(userWReceivers);
        RemoteReceiver receiver = remoteReceiverService.createRemoteReceiver(name, description, owner);
        assertTrue(receiver.getRemoteReceiverID() > 0L);
        assertNotNull(receiver.getRemoteReceiverKey());
    }

    @Test
    public void testFindRemoteReceiverLong() {
        final String name = "TestFindRemoteReceiverLong";
        final String description = "TestFindRemoteReceiverLong description";
        User owner = userService.findUserByName(userWReceivers);
        RemoteReceiver newReceiver = remoteReceiverService.createRemoteReceiver(name, description, owner);
        RemoteReceiver found = remoteReceiverService.findRemoteReceiver(newReceiver.getRemoteReceiverID());
        assertEquals(newReceiver, found);
    }

    @Test
    public void testFindRemoteReceiverString() {
        final String name = "TestFindRemoteReceiverString";
        final String description = "TestFindRemoteReceiverString description";
        User owner = userService.findUserByName(userWReceivers);
        RemoteReceiver newReceiver = remoteReceiverService.createRemoteReceiver(name, description, owner);
        RemoteReceiver found = remoteReceiverService.findRemoteReceiver(newReceiver.getRemoteReceiverName());
        assertEquals(newReceiver, found);
    }

    @Test
    public void testUpdateRemoteReceiver() {
        final String name = "TestUpdateRemoteReceiver";
        final String description = "TestUpdateRemoteReceiver description";
        final String updatedDescription = "TestUpdateRemoteReceiver updated description";
        User owner = userService.findUserByName(userWReceivers);
        RemoteReceiver receiver = remoteReceiverService.createRemoteReceiver(name, description, owner);
        receiver.setDescription(updatedDescription);
        RemoteReceiver updatedReceiver = remoteReceiverService.updateRemoteReceiver(receiver);
        assertEquals(updatedDescription, updatedReceiver.getDescription());
    }

    @Test
    public void testFindByOwner() {
        User ownNothinger = userService.findUserByName(userWOReceivers);
        List<RemoteReceiver> empty = remoteReceiverService.findByOwner(ownNothinger);
        assertEquals(0, empty.size());
        final String name = "TestFindByOwnerReceiver";
        final String description = "TestFindByOwnerReceiver description";
        User owner = userService.findUserByName(userWReceivers);
        RemoteReceiver receiver = remoteReceiverService.createRemoteReceiver(name, description, owner);
        List<RemoteReceiver> list = remoteReceiverService.findByOwner(owner);
        List<RemoteReceiver> filtered = list.stream()
                .flatMap(rec -> name.equals(rec.getRemoteReceiverName()) ? Stream.of(rec) : Stream.empty())
                .collect(Collectors.toList());
        assertEquals(1, filtered.size());
        assertEquals(receiver, filtered.get(0));
    }

    @Test
    public void testRemoveRemoteReceiver() {
        final String name = "TestRemoveRemoteReceiver";
        final String description = "TestRemoveRemoteReceiver description";
        User owner = userService.findUserByName(userWReceivers);
        final RemoteReceiver receiver = remoteReceiverService.createRemoteReceiver(name, description, owner);
        final RemoteReceiver found = remoteReceiverService.findRemoteReceiver(receiver.getRemoteReceiverID());
        assertEquals(receiver, found);
        remoteReceiverService.removeRemoteReceiver(receiver);
        final RemoteReceiver afterRemoval = remoteReceiverService.findRemoteReceiver(receiver.getRemoteReceiverID());
        assertNotEquals(receiver, afterRemoval);
        remoteReceiverService.removeRemoteReceiver(receiver);
    }
}
