package adsbrecorder.user.controller;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import adsbrecorder.common.aop.annotation.PathEntity;
import adsbrecorder.user.UserServiceMappings;
import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.service.AuthorityService;
import adsbrecorder.user.service.RoleService;
import adsbrecorder.user.service.UserService;

@RestController
public class UserManagementController implements UserServiceMappings {

    private UserService userService;
    private RoleService roleService;
    private AuthorityService authorityService;

    @Autowired
    public UserManagementController(UserService userService, RoleService roleService, AuthorityService authorityService) {
        this.userService = requireNonNull(userService);
        this.roleService = requireNonNull(roleService);
        this.authorityService = requireNonNull(authorityService);
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
    public ResponseEntity<Map<String, Object>> viewUserRoles(@PathEntity(name = "user") User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("userId", user.getUserId(),
                             "userRoles", user.getUserRoles()));
    }

    @GetMapping(VIEW_USER_AUTHORITIES)
    public ResponseEntity<Map<String, Object>> viewUserAuthorities(@PathEntity(name = "user") User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("userId", user.getUserId(),
                             "authorities", user.getDirectAuthorities()));
    }

    @PostMapping(VIEW_USER_AUTHORITIES)
    public ResponseEntity<Map<String, Object>> assignAuthoritiesToUser(@PathEntity(name = "user") User user,
            @RequestBody List<Long> authorityIds) {
        final Set<Authority> currentAuthorities = user.getAuthorities();
        Set<Authority> authorities = authorityIds.stream().flatMap(authId -> {
            Authority auth = authorityService.findById(authId);
            if (auth == null || currentAuthorities.contains(auth))
                return Stream.empty();
            return Stream.of(auth);
        }).collect(Collectors.toSet());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("userId", user.getUserId(),
                             "authorities", this.authorityService.assignAuthoritiesToUser(user, authorities)));
    }

    @GetMapping(VIEW_USER_UNASSIGNED_AUTHORITIES)
    public ResponseEntity<Map<String, Object>> listUnassignedAuthoritiesForUser(@PathEntity(name = "user") User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("userId", user.getUserId(),
                             "authoritiesAvailable", this.authorityService.findAvailableAuthorities(user)));
    }

    @GetMapping(VIEW_USER_UNASSIGNED_ROLES)
    public ResponseEntity<Map<String, Object>> listUnassignedRolesForUser(@PathEntity(name = "user") User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("userId", user.getUserId(),
                             "rolesAvailable", this.roleService.findAvailableRoles(user)));
    }
}
