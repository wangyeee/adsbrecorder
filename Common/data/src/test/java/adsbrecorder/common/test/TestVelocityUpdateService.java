package adsbrecorder.common.test;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

import adsbrecorder.common.test.conf.InMemoryDBTestConfiguration;
import adsbrecorder.common.test.conf.EmbeddedMongoDBTestConfiguration;
import adsbrecorder.receiver.service.VelocityUpdateService;

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

    @Autowired
    VelocityUpdateService velocityUpdateService;

    @Test
    public void testInterleavingTrackingRecords() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> velocityUpdateService.interleavingTrackingRecords(1, System.currentTimeMillis()));
    }
}
