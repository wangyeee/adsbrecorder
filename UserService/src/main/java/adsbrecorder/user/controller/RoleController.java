package adsbrecorder.user.controller;

import static java.util.Objects.requireNonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.user.UserServiceMappings;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.RoleService;
import adsbrecorder.user.service.UserRoleService;
import adsbrecorder.user.service.UserService;

@RestController
public class RoleController implements UserServiceMappings {

    private RoleService roleService;
    private UserRoleService userRoleService;
    private UserService userService;

    @Autowired
    public RoleController(RoleService roleService, UserRoleService userRoleService, UserService userService) {
        this.roleService = requireNonNull(roleService);
        this.userService = requireNonNull(userService);
        this.userRoleService = requireNonNull(userRoleService);
    }

    @PostMapping(ROLE_ASSIGNED_USERS)
    public ResponseEntity<Object> assignRoleToUser(@PathVariable("role") Long roleId, @RequestParam("user") Long userId) {
        User user = this.userService.findUserById(userId);
        Role role = this.roleService.findRoleById(roleId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRoleService.assignRoleToUser(role, user));
    }
}
