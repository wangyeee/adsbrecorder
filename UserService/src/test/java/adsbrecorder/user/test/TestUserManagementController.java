package adsbrecorder.user.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import adsbrecorder.user.UserServiceMappings;
import adsbrecorder.user.controller.UserController;
import adsbrecorder.user.controller.UserManagementController;
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
public class TestUserManagementController implements UserServiceMappings {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy filterChainProxy;

    private MockMvc mockMvc;

    private final String adminUsername = "Admin";
    private final String adminPassword = "Admin";

    @PostConstruct
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(filterChainProxy).build();
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
            final String jwt = "Bearer " + getLoginToken(adminUsername, adminPassword);
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
            Cookie cookie = getLoginCookie(adminUsername, adminPassword);
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
            Map<String, Object> map = login(adminUsername, adminPassword);
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
            Map<String, Object> map = login(adminUsername, adminPassword);
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
            Map<String, Object> map = login(adminUsername, adminPassword);
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

    private Map<String, Object> login(String username, String password) throws Exception {
        final String name = "JWT-AUTH";
        Cookie cookie0 = null;
        MockHttpServletResponse response = mockMvc.perform(
                post(USER_LOGIN)
                .param("username", username)
                .param("password", password))
            .andExpect(status().isOk())
            .andExpect(cookie().exists(name))
            .andReturn()
            .getResponse();
        Cookie[] cookies = response.getCookies();
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie0 = cookie;
                break;
            }
        }
        JSONObject jsonObject = new JSONObject(response.getContentAsString());
        return Map.of("token", "Bearer " + cookie0.getValue(),
                "cookie", cookie0,
                "user", jsonObject.get("user"));
    }

    private String getLoginToken(String username, String password) throws Exception {
        return String.valueOf(login(username, password).get("token"));
    }

    private Cookie getLoginCookie(String username, String password) throws Exception {
        return (Cookie) login(username, password).get("cookie");
    }
}
