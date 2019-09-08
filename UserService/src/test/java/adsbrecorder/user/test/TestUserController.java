package adsbrecorder.user.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import adsbrecorder.user.UserServiceMappings;
import adsbrecorder.user.controller.RoleController;
import adsbrecorder.user.controller.UserController;
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
@WebMvcTest(controllers = {UserController.class, RoleController.class})
public class TestUserController implements UserServiceMappings {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy filterChainProxy;

    private MockMvc mockMvc;

    private final String newUsername = "TestUser";
    private final String newPassword = "TestUser Password";

    private final String adminUsername = "Admin";
    private final String adminPassword = "Admin";

    private final String jwtCookieName = "JWT-AUTH";

    @PostConstruct
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(filterChainProxy).build();
    }

    @Test
    public void testUserRegister() {
        try {
            registerUser(newUsername, newPassword);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testUsernameExistsCheck() {
        try {
            MockHttpServletResponse response = mockMvc.perform(
                    get(USERNAME_CHECK)
                    .param("username", "Admin"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
            JSONObject jsonObject = new JSONObject(response.getContentAsString());
            assertEquals("true", jsonObject.get("exist"));
            response = mockMvc.perform(
                    get(USERNAME_CHECK)
                    .param("username", "AdminNotExists"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
            jsonObject = new JSONObject(response.getContentAsString());
            assertEquals("false", jsonObject.get("exist"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testUserLoginFromJWT() {
        try {
            MockHttpServletResponse loginResponse = mockMvc.perform(
                    post(USER_LOGIN)
                    .param("username", adminUsername)
                    .param("password", adminPassword))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(jwtCookieName))
                .andReturn()
                .getResponse();
            JSONObject jsonObject = new JSONObject(loginResponse.getContentAsString());
            assertNotNull(jsonObject.get("user"));
            Cookie cookie0 = null;
            Cookie[] cookies = loginResponse.getCookies();
            for (Cookie cookie : cookies) {
                if (jwtCookieName.equals(cookie.getName())) {
                    cookie0 = cookie;
                    break;
                }
            }
            MockHttpServletResponse cookieResponse = mockMvc.perform(
                    get(USER_LOGIN).cookie(cookie0))
                    .andExpect(status().isOk()).andReturn().getResponse();
            assertNotNull(cookieResponse.getContentAsString());
            JSONObject cookieJson = new JSONObject(cookieResponse.getContentAsString());
            assertNotNull(cookieJson.get("user"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testUserLogin() {
        try {
            MockHttpServletResponse response = mockMvc.perform(
                    post(USER_LOGIN)
                    .param("username", adminUsername)
                    .param("password", adminPassword))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(jwtCookieName))
                .andReturn()
                .getResponse();
            JSONObject jsonObject = new JSONObject(response.getContentAsString());
            assertNotNull(jsonObject.get("user"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testUserLogout() {
        try {
            MockHttpServletResponse loginResponse = mockMvc.perform(
                    post(USER_LOGIN)
                    .param("username", adminUsername)
                    .param("password", adminPassword))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(jwtCookieName))
                .andReturn()
                .getResponse();
            Cookie cookie0 = null;
            Cookie[] cookies = loginResponse.getCookies();
            for (Cookie cookie : cookies) {
                if (jwtCookieName.equals(cookie.getName())) {
                    cookie0 = cookie;
                    break;
                }
            }
            MockHttpServletResponse logoutResponse = mockMvc.perform(
                get(USER_LOGOUT).cookie(cookie0))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
            JSONObject jsonObject = new JSONObject(logoutResponse.getContentAsString());
            assertEquals(Boolean.TRUE, jsonObject.get("logout"));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testAssignRoleToUser() {
        try {
            MockHttpServletResponse loginResponse = mockMvc.perform(
                    post(USER_LOGIN)
                    .param("username", adminUsername)
                    .param("password", adminPassword))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(jwtCookieName))
                .andReturn()
                .getResponse();
            Long roleId = -1L;
            final String newRoleName = "REPORT_USER";
            JSONObject loginJson = new JSONObject(loginResponse.getContentAsString());
            JSONArray roles = loginJson.getJSONObject("user").optJSONArray("userRoles");
            for (Object role0 : roles) {
                JSONObject role = ((JSONObject) role0).getJSONObject("role");
                if (newRoleName.equals(role.get("roleName"))) {
                    roleId = Long.valueOf(String.valueOf(role.get("roleId")));
                    break;
                }
            }
            Cookie cookie0 = null;
            Cookie[] cookies = loginResponse.getCookies();
            for (Cookie cookie : cookies) {
                if (jwtCookieName.equals(cookie.getName())) {
                    cookie0 = cookie;
                    break;
                }
            }
            Long newUserId = registerUser("TestAssignRoleToUser", "TestAssignRoleToUserPassword");
            MockHttpServletResponse assignRoleResponse = mockMvc.perform(
                    post(ROLE_ASSIGNED_USERS, String.valueOf(roleId))
                    .param("user", String.valueOf(newUserId)).cookie(cookie0))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
            JSONObject jsonObject = new JSONObject(assignRoleResponse.getContentAsString());
            assertNotNull(jsonObject);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void testRemoveRoleFromUser() {
        try {
            MockHttpServletResponse loginResponse = mockMvc.perform(
                    post(USER_LOGIN)
                    .param("username", adminUsername)
                    .param("password", adminPassword))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(jwtCookieName))
                .andReturn()
                .getResponse();
            Long roleId = -1L;
            final String newRoleName = "REPORT_USER";
            JSONObject loginJson = new JSONObject(loginResponse.getContentAsString());
            JSONArray roles = loginJson.getJSONObject("user").optJSONArray("userRoles");
            for (Object role0 : roles) {
                JSONObject role = ((JSONObject) role0).getJSONObject("role");
                if (newRoleName.equals(role.get("roleName"))) {
                    roleId = Long.valueOf(String.valueOf(role.get("roleId")));
                    break;
                }
            }
            Cookie cookie0 = null;
            Cookie[] cookies = loginResponse.getCookies();
            for (Cookie cookie : cookies) {
                if (jwtCookieName.equals(cookie.getName())) {
                    cookie0 = cookie;
                    break;
                }
            }
            Long newUserId = registerUser("TestRemoveRoleFromUser", "TestRemoveRoleFromUserPassword");
            MockHttpServletResponse assignRoleResponse = mockMvc.perform(
                    post(ROLE_ASSIGNED_USERS, String.valueOf(roleId))
                    .param("user", String.valueOf(newUserId)).cookie(cookie0))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
            JSONObject jsonObject = new JSONObject(assignRoleResponse.getContentAsString());
            assertNotNull(jsonObject);
            mockMvc.perform(
                    delete(ROLE_ASSIGNED_USERS, String.valueOf(roleId))
                    .param("user", String.valueOf(newUserId)).cookie(cookie0))
                .andExpect(status().isOk());
            mockMvc.perform(
                    delete(ROLE_ASSIGNED_USERS, String.valueOf(roleId))
                    .param("user", String.valueOf(newUserId)).cookie(cookie0))
                .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e);
        }
    }

    private Long registerUser(String username, String password) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                post(USER_NEW)
                .param("username", username)
                .param("password", password))
            .andExpect(status().isCreated())
            .andReturn().getResponse();
        JSONObject json = new JSONObject(response.getContentAsString());
        assertNotNull(json.get("newUser"));
        JSONObject newUser = json.getJSONObject("newUser");
        return Long.valueOf(String.valueOf(newUser.get("userId")));
    }
}
