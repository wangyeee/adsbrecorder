package adsbrecorder.common.test.user;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.service.RoleService;

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
public class TestRoleService {

    @Autowired
    public RoleService roleService;

    @Test
    public void testFindOrSaveByRoleName() {
        String roleName = "ROLE_TEST_FIND_OR_SAVE_BY_ROLE_NAME";
        String displayName = "Test Role Display Name";
        String description = "Test role description";

        Role newRole = roleService.findOrSaveByRoleName(roleName, displayName, description);
        assertNotNull(newRole);
        Role updatedRole = roleService.findOrSaveByRoleName(roleName, displayName + " updated", description + " updated");
        assertNotEquals(updatedRole, newRole);
    }

    @Test
    public void testFindByRoleName() {
        String roleName = "ROLE_TEST_FIND_BY_ROLE_NAME";
        String displayName = "Test Role Display Name";
        String description = "Test role description";
        
        Role newRole = roleService.findOrSaveByRoleName(roleName, displayName, description);
        assertNotNull(newRole);
        Role findByName = roleService.findByRoleName(roleName);
        assertEquals(newRole, findByName);
        Role nonExists = roleService.findByRoleName("Some Role");
        assertEquals(Long.valueOf(-1L), nonExists.getRoleId());
        Role nullName = roleService.findByRoleName(null);
        assertEquals(Long.valueOf(-1L), nullName.getRoleId());
    }

    @Test
    public void testFindRoleById() {
        String roleName = "ROLE_TEST_FIND_BY_ROLE_NAME";
        String displayName = "Test Role Display Name";
        String description = "Test role description";
        
        Role newRole = roleService.findOrSaveByRoleName(roleName, displayName, description);
        assertNotNull(newRole);
        Role findById = roleService.findRoleById(newRole.getRoleId());
        assertEquals(newRole, findById);
        Role nonExists = roleService.findRoleById(-2L);
        assertEquals(Long.valueOf(-1L), nonExists.getRoleId());
    }

    @Test
    public void testSaveRole() {
        String roleName = "ROLE_TEST_SAVE_ROLE";
        String displayName = "Test Role Display Name";
        String description = "Test role description";
        Role newRole = new Role();
        newRole.setDisplayName(displayName);
        newRole.setRoleName(roleName);
        newRole.setDescription(description);
        Role savedRole = roleService.saveRole(newRole);
        assertEquals(roleName, savedRole.getRoleName());
        assertEquals(displayName, savedRole.getDisplayName());
        assertEquals(description, savedRole.getDescription());
    }
}
