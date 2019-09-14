package adsbrecorder.user.service;

import java.util.List;

import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.User;

public interface RoleService {

    Role findOrSaveByRoleName(String roleName, String displayName, String description);
    Role findByRoleName(String role);
    Role findRoleById(Long roleId);
    Role saveRole(Role role);
    List<Role> findAvailableRoles(User user);
}
