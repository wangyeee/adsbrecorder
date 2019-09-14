package adsbrecorder.user;

import adsbrecorder.common.utils.URLUtils;

public interface UserServiceMappings extends URLUtils {

    String USER_NEW = "/api/user/reg";
    String USER_LOGIN = "/api/user/login";
    String USER_LOGOUT = "/api/user/logout";
    String USERNAME_CHECK = "/api/user/namecheck";

    String ROLE_ASSIGNED_USERS = "/api/role/admin/{role}/users";
    String LIST_OF_USERS = "/api/user/admin/list";
    String VIEW_USER_ROLES = "/api/user/admin/detail/{user}/roles";
    String VIEW_USER_UNASSIGNED_ROLES = "/api/user/admin/detail/{user}/unassignedroles";
    String VIEW_USER_UNASSIGNED_AUTHORITIES = "/api/user/admin/detail/{user}/unassignedauthorities";
    String VIEW_USER_AUTHORITIES = "/api/user/admin/detail/{user}/authorities";
}
