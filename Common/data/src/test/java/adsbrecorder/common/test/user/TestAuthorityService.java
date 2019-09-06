package adsbrecorder.common.test.user;

import static org.junit.Assert.assertNotNull;
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
import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.service.AuthorityService;

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
public class TestAuthorityService {

    @Autowired
    public AuthorityService authorityService;

    @Test
    public void testCreateAuthority() {
        String authority = "TEST_CREATE_AUTHORITY";
        String displayName = "Test Create Authority Display Name";
        String description = "Test Create Authority Description";
        Authority created = createAuthorityHelper(authority, displayName, description);
        assertNotNull(created.getAuthorityId());
        assertTrue(created.getAuthorityId() > 0L);
    }

    private Authority createAuthorityHelper(String authority, String displayName, String description) {
        Authority auth = new Authority();
        auth.setAuthority(authority);
        auth.setDisplayName(displayName);
        auth.setDescription(description);
        return authorityService.createAuthority(auth);
    }
}
