package adsbrecorder.user.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import adsbrecorder.user.controller.UserController;
import adsbrecorder.user.controller.UserManagementController;
import adsbrecorder.user.service.AuthorityService;
import adsbrecorder.user.service.RoleService;
import adsbrecorder.user.service.impl.AuthorityServiceImpl;
import adsbrecorder.user.service.impl.RoleServiceImpl;
import adsbrecorder.user.test.conf.InMemoryDBTestConfiguration;

@EnableJpaRepositories(basePackages = "adsbrecorder.user.repo")
@TestPropertySource(locations = "/test.properties")
@ContextConfiguration(classes = InMemoryDBTestConfiguration.class)
@ComponentScan(basePackages = {
        "adsbrecorder.common.aop.conf",
        "adsbrecorder.user.test.conf",
        "adsbrecorder.user.lc",
        "adsbrecorder.user.security",
        "adsbrecorder.user.controller",
        "adsbrecorder.user.service"})
@EntityScan(basePackages = {
        "adsbrecorder.user.entity"})
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {UserManagementController.class, UserController.class})
public class TestUserManagementController implements TestUserUtils {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FilterChainProxy filterChainProxy;

    private MockMvc mockMvc;

    private final String adminUsername = "Admin";
    private final String adminPassword = "Admin";

    @PostConstruct
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(filterChainProxy).build();
        // reload data created in Init.loadDefaultUserRoles()
        ((AuthorityServiceImpl) this.authorityService).cacheAllAuthorities();
        ((RoleServiceImpl) this.roleService).cacheAllRoles();
    }

    @Test
    public void testListOfUsersNoLogin() {
        try {
            mockMvc.perform(get(LIST_OF_USERS))
            .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testListOfUsersAdminLoginHeader() {
        try {
            final String jwt = "Bearer " + getLoginToken(mockMvc, adminUsername, adminPassword);
            MockHttpServletResponse response = mockMvc.perform(
                    get(LIST_OF_USERS).header("Authorization", jwt))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertNotNull(response.getContentAsString());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testListOfUsersAdminLoginCookie() {
        try {
            Cookie cookie = getLoginCookie(mockMvc, adminUsername, adminPassword);
            MockHttpServletResponse response = mockMvc.perform(
                    get(LIST_OF_USERS).cookie(cookie))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertNotNull(response.getContentAsString());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testViewUserRoles() {
        try {
            Map<String, Object> map = login(mockMvc, adminUsername, adminPassword);
            final String jwt = String.valueOf(map.get("token"));
            final long userId = Long.parseLong(String.valueOf(((JSONObject)map.get("user")).get("userId")));
            MockHttpServletResponse response = mockMvc.perform(
                    get(VIEW_USER_ROLES, userId).header("Authorization", jwt))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertNotNull(response.getContentAsString());
            mockMvc.perform(
                    get(VIEW_USER_ROLES, userId + 1).header("Authorization", jwt))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testViewUserAuthorities() {
        try {
            Map<String, Object> map = login(mockMvc, adminUsername, adminPassword);
            final String jwt = String.valueOf(map.get("token"));
            final long userId = Long.parseLong(String.valueOf(((JSONObject)map.get("user")).get("userId")));
            MockHttpServletResponse response = mockMvc.perform(
                    get(VIEW_USER_AUTHORITIES, userId).header("Authorization", jwt))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertNotNull(response.getContentAsString());
            mockMvc.perform(
                    get(VIEW_USER_AUTHORITIES, userId + 1).header("Authorization", jwt))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testListUnassignedRolesForUser() {
        try {
            Map<String, Object> map = login(mockMvc, adminUsername, adminPassword);
            final String jwt = String.valueOf(map.get("token"));
            final long userId = Long.parseLong(String.valueOf(((JSONObject)map.get("user")).get("userId")));
            MockHttpServletResponse response = mockMvc.perform(
                    get(VIEW_USER_UNASSIGNED_ROLES, userId).header("Authorization", jwt))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertNotNull(response.getContentAsString());
            mockMvc.perform(
                    get(VIEW_USER_UNASSIGNED_ROLES, userId + 1).header("Authorization", jwt))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testListUnassignedAuthoritiesForUser() {
        final String username = "ListUnassignedAuthoritiesForUser";
        final String password = "ListUnassignedAuthoritiesForUser Password";
        try {
            final long userId = registerUser(mockMvc, username, password);
            Map<String, Object> map = login(mockMvc, adminUsername, adminPassword);
            final String jwt = String.valueOf(map.get("token"));
            MockHttpServletResponse response = mockMvc.perform(
                    get(VIEW_USER_UNASSIGNED_AUTHORITIES, userId).header("Authorization", jwt))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertNotNull(response.getContentAsString());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testAssignAuthoritiesToUser() {
        final String username = "AssignAuthoritiesToUser";
        final String password = "AssignAuthoritiesToUser Password";
        try {
            final long userId = registerUser(mockMvc, username, password);
            Map<String, Object> map = login(mockMvc, adminUsername, adminPassword);
            final String jwt = String.valueOf(map.get("token"));
            MockHttpServletResponse getAuthResponse = mockMvc.perform(
                    get(VIEW_USER_UNASSIGNED_AUTHORITIES, userId).header("Authorization", jwt))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertNotNull(getAuthResponse.getContentAsString());
            JSONArray unassignedAuthorities = new JSONObject(getAuthResponse.getContentAsString()).getJSONArray("authoritiesAvailable");
            List<Long> ids = unassignedAuthorities.toList().stream().flatMap(auth0 -> {
                Map<?, ?> auth1 = (Map<?, ?>) auth0;
                if ("RUN_SIMPLE_DAILY_SUMMARY_REPORT".equals(auth1.get("authority")))
                    return Stream.of(Long.valueOf(String.valueOf(auth1.get("authorityId"))));
                if ("VIEW_REPORT_METADATA".equals(auth1.get("authority")))
                    return Stream.of(Long.valueOf(String.valueOf(auth1.get("authorityId"))));
                return Stream.empty();
            }).collect(Collectors.toList());
            MockHttpServletResponse assignAuthResponse = mockMvc.perform(
                    post(VIEW_USER_AUTHORITIES, userId)
                    .header("Authorization", jwt)
                    .content(new JSONArray(ids).toString())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse();
            assertNotNull(assignAuthResponse.getContentAsString());
            JSONArray newAuth = new JSONObject(assignAuthResponse.getContentAsString()).getJSONArray("authorities");
            assertTrue(newAuth.length() > 0);
        } catch (Exception e) {
            fail(e);
        }
    }
}
