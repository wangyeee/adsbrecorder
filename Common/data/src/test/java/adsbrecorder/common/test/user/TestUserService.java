package adsbrecorder.common.test.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import adsbrecorder.common.test.conf.InMemoryDBTestConfiguration;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserService;

@EnableJpaRepositories(basePackages = "adsbrecorder.user.repo")
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = InMemoryDBTestConfiguration.class)
@ComponentScan(basePackages = {
        "adsbrecorder.user",
        "adsbrecorder.user.service"})
@EntityScan(basePackages = {
        "adsbrecorder.user.entity"})
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestUserService {

    @Autowired
    public UserService userService;

    @Test
    public void testIsUsernameExist() {
        String username = "Some name";
        boolean exist = userService.isUsernameExist(username);
        assertFalse(exist);
    }

    @Test
    public void testCreateNewUser() {
        String username = "TestUser";
        String password = "TestUserPassword";
        User newUser = userService.createNewUser(username, password);
        assertTrue(newUser.getUserId() > 0L);
        boolean exist = userService.isUsernameExist(newUser.getUsername());
        assertTrue(exist);
    }
}
