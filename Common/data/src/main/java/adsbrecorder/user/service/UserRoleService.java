package adsbrecorder.user.service;

import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserRole;

public interface UserRoleService {

    UserRole assignRoleToUser(Role role, User user);

}
