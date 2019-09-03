package adsbrecorder.user.controller;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.user.UserServiceMappings;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.RoleService;
import adsbrecorder.user.service.UserService;

@RestController
public class UserManagementController implements UserServiceMappings {

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public UserManagementController(UserService userService, RoleService roleService) {
        this.userService = requireNonNull(userService);
        this.roleService = requireNonNull(roleService);
    }

    @GetMapping(LIST_OF_USERS)
    public ResponseEntity<Map<String, Object>> listOfUsers(HttpServletRequest request,
            @RequestParam(name = PAGE_NUMBER_URL_KEY, required = false, defaultValue = "1") int page,
            @RequestParam(name = AMOUNT_PER_PAGE_URL_KEY, required = false, defaultValue = "5") int amount) {
        if (page <= 0) page = -page;
        if (page > 0) page--;
        if (amount < 0) amount = -amount;
        if (amount == 0) amount = 5;
        long[] count = new long[1];
        List<User> users = this.userService.findUsers(request.getParameterMap(), page, amount, count);
        if (users.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No user found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("users", users,
                             "totalCount", count[0]));
    }

    @GetMapping(VIEW_USER_ROLES)
    public ResponseEntity<Map<String, Object>> viewUserRoles(@PathVariable(name = "user") Long userId) {
        User user = userService.findUserById(userId);
        if (user.getUserId() == -1L) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No user found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("userId", userId,
                             "userRoles", user.getUserRoles()));
    }

    @GetMapping(VIEW_USER_AUTHORITIES)
    public ResponseEntity<Map<String, Object>> viewUserAuthorities(@PathVariable(name = "user") Long userId) {
        User user = userService.findUserById(userId);
        if (user.getUserId() == -1L) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No user found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("userId", userId,
                             "authorities", user.getAuthorities()));
    }

    @GetMapping(VIEW_USER_UNASSIGNED_ROLES)
    public ResponseEntity<Map<String, Object>> listUnassignedRolesForUser(@PathVariable(name = "user") Long userId) {
        User user = userService.findUserById(userId);
        if (user.getUserId() == -1L) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No user found"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("userId", userId,
                             "rolesAvailable", this.roleService.findAvailableRoles(user)));
    }
}
