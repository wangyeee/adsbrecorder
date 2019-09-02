package adsbrecorder.user.service;

import java.util.Set;

import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserRole;

public interface UserRoleService {

    UserRole assignRoleToUser(Role role, User user);
    User assignDefaultRolesToUser(User user);
    Set<Role> getRolesForUser(User user);
    Set<UserRole> getUserRoles(User user);
}
