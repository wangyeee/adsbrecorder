package adsbrecorder.user.test;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import javax.servlet.http.Cookie;

import org.json.JSONObject;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import adsbrecorder.user.UserServiceMappings;

public interface TestUserUtils extends UserServiceMappings {

    default Long registerUser(MockMvc mockMvc, String username, String password) throws Exception {
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

    default Map<String, Object> login(MockMvc mockMvc, String username, String password) throws Exception {
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

    default String getLoginToken(MockMvc mockMvc, String username, String password) throws Exception {
        return String.valueOf(login(mockMvc, username, password).get("token"));
    }

    default Cookie getLoginCookie(MockMvc mockMvc, String username, String password) throws Exception {
        return (Cookie) login(mockMvc, username, password).get("cookie");
    }
}
