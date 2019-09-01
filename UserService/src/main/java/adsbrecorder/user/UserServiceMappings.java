package adsbrecorder.user;

import adsbrecorder.common.utils.URLUtils;

public interface UserServiceMappings extends URLUtils {

    String USER_NEW = "/api/user/reg";
    String USER_LOGIN = "/api/user/login";
    String USER_LOGOUT = "/api/user/logout";
    String USERNAME_CHECK = "/api/user/namecheck";

    String ROLE_ASSIGNED_USERS = "/api/role/{role}/users";

    String LIST_OF_USERS = "/api/user/list";
}
