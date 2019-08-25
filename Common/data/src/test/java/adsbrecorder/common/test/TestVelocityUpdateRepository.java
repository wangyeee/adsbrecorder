package adsbrecorder.common.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import adsbrecorder.common.test.conf.MongoDBTestConfiguration;
import adsbrecorder.receiver.entity.VelocityUpdate;
import adsbrecorder.receiver.repo.VelocityUpdateRepository;

@EnableMongoRepositories(basePackages = {"adsbrecorder.receiver.repo"})
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = MongoDBTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestVelocityUpdateRepository {

    @Autowired
    VelocityUpdateRepository velocityUpdateRepository;

    @Test
    public void testFindAllActiveByAddressICAO() {
        List<VelocityUpdate> updates = velocityUpdateRepository.findAllActiveByAddressICAO(13117252);
        updates.forEach(update -> System.err.println(update));
        assertEquals(updates.size(), 36);
    }
}
