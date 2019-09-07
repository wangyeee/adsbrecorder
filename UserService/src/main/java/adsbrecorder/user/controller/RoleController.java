package adsbrecorder.user.controller;

import static java.util.Objects.requireNonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.common.aop.annotation.PathEntity;
import adsbrecorder.common.aop.annotation.RequestEntity;
import adsbrecorder.user.UserServiceMappings;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.UserRoleService;

@RestController
public class RoleController implements UserServiceMappings {

    private UserRoleService userRoleService;

    @Autowired
    public RoleController(UserRoleService userRoleService) {
        this.userRoleService = requireNonNull(userRoleService);
    }

    @PostMapping(ROLE_ASSIGNED_USERS)
    public ResponseEntity<Object> assignRoleToUser(@PathEntity("role") Role role, @RequestEntity("user") User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRoleService.assignRoleToUser(role, user));
    }
}
