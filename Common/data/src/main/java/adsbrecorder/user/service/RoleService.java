package adsbrecorder.user.service;

import adsbrecorder.user.entity.Role;

public interface RoleService {

    Role findByRoleName(String role);
    Role findRoleById(Long roleId);
    Role saveRole(Role role);
}
