package adsbrecorder.common.test.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;

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

import adsbrecorder.common.test.conf.InMemoryDBTestConfiguration;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserRole;
import adsbrecorder.user.service.RoleService;
import adsbrecorder.user.service.UserRoleService;
import adsbrecorder.user.service.UserService;
import adsbrecorder.user.service.impl.UserRoleServiceImpl;

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
public class TestUserRoleService {

    @Autowired
    public UserRoleService userRoleService;

    @Autowired
    public RoleService roleService;

    @Autowired
    public UserService userService;

    private final String username1 = "TestUser1";
    private final String username2 = "TestUser2";
    private final String username3 = "TestUser3";
    private final String username4 = "TestUser4";
    private final String roleName1 = "TEST_ROLE1";
    private final String roleName2 = "TEST_ROLE2";
    private final String roleName3 = "TEST_ROLE3";
    private final String defaultRoleName1 = "TEST_DEFAULT_ROLE1";
    private final String defaultRoleName2 = "TEST_DEFAULT_ROLE2";

    private static boolean testDataLoaded = false;

    @PostConstruct
    public void createTestRolesAndUsers() {
        if (testDataLoaded) return;
        roleService.findOrSaveByRoleName(this.defaultRoleName1, "Test Default Role 1 Display Name", "Test Default Role 1 Description");
        roleService.findOrSaveByRoleName(this.defaultRoleName2, "Test Default Role 2 Display Name", "Test Default Role 2 Description");
        roleService.findOrSaveByRoleName(this.roleName1, "Test Role 1 Display Name", "Test Role 1 Description");
        roleService.findOrSaveByRoleName(this.roleName2, "Test Role 2 Display Name", "Test Role 2 Description");
        roleService.findOrSaveByRoleName(this.roleName3, "Test Role 3 Display Name", "Test Role 3 Description");
        ((UserRoleServiceImpl) userRoleService).loadDefaultRoles();
        userService.createNewUser(this.username1, "Test User 1 Password");
        userService.createNewUser(this.username2, "Test User 2 Password");
        userService.createNewUser(this.username3, "Test User 3 Password");
        userService.createNewUser(this.username4, "Test User 4 Password");
        testDataLoaded = true;
    }

    @Test
    public void testAssignDefaultRolesToUser() {
        User testUser1 = userService.findUserByName(this.username1);
        userRoleService.assignDefaultRolesToUser(testUser1);
        assertEquals(testUser1.getUserRoles().size(), 2);
    }

    @Test
    public void testAssignRoleToUser() {
        User testUser2 = userService.findUserByName(this.username2);
        Role role1 = roleService.findByRoleName(this.roleName1);
        Set<Role> rolesPrev = testUser2.getUserRoles().stream().map(ur -> ur.getRole()).collect(Collectors.toSet());
        assertFalse(rolesPrev.contains(role1));
        userRoleService.assignRoleToUser(role1, testUser2);
        User reload = userService.findUserByName(this.username2);
        Set<Role> roles = reload.getUserRoles().stream().map(ur -> ur.getRole()).collect(Collectors.toSet());
        assertTrue(roles.contains(role1));
    }

    @Test
    public void testGetRolesForUser() {
        User testUser3 = userService.findUserByName(this.username3);
        Role role2 = roleService.findByRoleName(this.roleName2);
        userRoleService.assignRoleToUser(role2, testUser3);
        Set<Role> roles = userRoleService.getRolesForUser(testUser3);
        roles.forEach(role -> assertTrue(role.getRoleName().equals(this.defaultRoleName1)
                || role.getRoleName().equals(this.defaultRoleName2)
                || role.getRoleName().equals(this.roleName2)));
    }

    @Test
    public void testGetUserRoles() {
        User testUser4 = userService.findUserByName(this.username4);
        Role role3 = roleService.findByRoleName(this.roleName3);
        userRoleService.assignRoleToUser(role3, testUser4);
        Set<UserRole> userRoles = userRoleService.getUserRoles(testUser4);
        userRoles.forEach(userRole -> assertTrue(userRole.getRole().getRoleName().equals(this.defaultRoleName1)
                || userRole.getRole().getRoleName().equals(this.defaultRoleName2)
                || userRole.getRole().getRoleName().equals(this.roleName3)));
    }
}
