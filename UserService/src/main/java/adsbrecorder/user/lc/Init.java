package adsbrecorder.user.lc;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.RoleAuthority;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserRole;
import adsbrecorder.user.entity.UserRoleType;
import adsbrecorder.user.repo.AuthorityRepository;
import adsbrecorder.user.repo.RoleAuthorityRepository;
import adsbrecorder.user.repo.UserRoleRepository;
import adsbrecorder.user.service.RoleService;
import adsbrecorder.user.service.UserService;

@Component
public class Init {

    private UserService userService;
    private RoleService roleService;
    private UserRoleRepository userRoleRepository;
    private AuthorityRepository authorityRepository;
    private RoleAuthorityRepository roleAuthorityRepository;

    @Autowired
    public Init(UserService userService,
            RoleService roleService,
            UserRoleRepository userRoleRepository,
            AuthorityRepository authorityRepository,
            RoleAuthorityRepository roleAuthorityRepository) {
        this.userService = requireNonNull(userService);
        this.roleService = requireNonNull(roleService);
        this.userRoleRepository = requireNonNull(userRoleRepository);
        this.authorityRepository = requireNonNull(authorityRepository);
        this.roleAuthorityRepository = requireNonNull(roleAuthorityRepository);
    }

    public void loadDefaultUserRoles(Resource resource) {
        try (InputStream in = resource.getInputStream()) {
            String jsonTxt = IOUtils.toString(in, "UTF-8");
            JSONObject json = new JSONObject(jsonTxt);
            processJSON(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processJSON(JSONObject json) {
        JSONArray roles = json.getJSONArray("roles");
        JSONArray users = json.getJSONArray("users");
        JSONArray userRoles = json.getJSONArray("userRoles");
        JSONArray authorities = json.getJSONArray("authorities");
        JSONArray roleAuthorities = json.getJSONArray("roleAuthorities");
        Map<String, Role> roleNames = new HashMap<String, Role>();
        Map<String, User> userNames = new HashMap<String, User>();
        Map<String, Authority> authorityNames = new HashMap<String, Authority>();
        roles.forEach(role -> {
            String displayName = ((JSONObject) role).getString("displayName");
            String roleName = ((JSONObject) role).getString("roleName");
            String description = ((JSONObject) role).getString("description");
            roleNames.put(roleName, roleService.findOrSaveByRoleName(roleName, displayName, description));
        });
        users.forEach(user -> {
            String username = ((JSONObject) user).getString("username");
            String password = ((JSONObject) user).getString("password");
            User exist = userService.findUserByName(username);
            if (exist == User.getUnauthorizedUser()) {
                userNames.put(username, userService.createNewUser(username, password));
            } else {
                userNames.put(username, exist);
            }
        });
        userRoles.forEach(userRole -> {
            String username = ((JSONObject) userRole).getString("user");
            User user = null;
            if (userNames.containsKey(username)) {
                user = userNames.get(username);
            } else {
                user = userService.findUserByName(username);
            }
            if (user != User.getUnauthorizedUser()) {
                JSONArray roleList = ((JSONObject) userRole).getJSONArray("roles");
                final User user0 = user;
                roleList.forEach(r -> {
                    String roleName = r.toString();
                    Role role = null;
                    if (roleNames.containsKey(roleName)) {
                        role = roleNames.get(roleName);
                    } else {
                        role = roleService.findByRoleName(roleName);
                    }
                    if (role.getRoleId() > 0L) {
                        Optional<UserRole> ur0 = userRoleRepository.findOneByUserAndRole(user0, role);
                        if (ur0.isEmpty()) {
                            UserRole newUserRole = new UserRole();
                            newUserRole.setUser(user0);
                            newUserRole.setRole(role);
                            newUserRole.setCreationDate(new Date());
                            newUserRole.setRoleType(UserRoleType.SYSTEM_ROLE);
                            userRoleRepository.save(newUserRole);
                        }
                    }
                });
            }
        });
        authorities.forEach(auth -> {
            Authority newAuthority = new Authority();
            newAuthority.setAuthority(((JSONObject) auth).getString("authority"));
            newAuthority.setDisplayName(((JSONObject) auth).getString("displayName"));
            newAuthority.setDescription(((JSONObject) auth).getString("description"));
            authorityNames.put(newAuthority.getAuthority(), authorityRepository.syncOne(newAuthority));
        });
        roleAuthorities.forEach(ra -> {
            String roleName = ((JSONObject) ra).getString("role");
            Role role = null;
            if (roleNames.containsKey(roleName)) {
                role = roleNames.get(roleName);
            } else {
                role = roleService.findByRoleName(roleName);
            }
            if (role.getRoleId() > 0L) {
                JSONArray authorityList = ((JSONObject) ra).getJSONArray("authorities");
                final Role role0 = role;
                authorityList.forEach(auth -> {
                    RoleAuthority roleAuthority = new RoleAuthority();
                    roleAuthority.setRole(role0);
                    String authorityName = auth.toString();
                    Authority authority = null;
                    if (authorityNames.containsKey(authorityName)) {
                        authority = authorityNames.get(authorityName);
                    } else {
                        Optional<Authority> oa = authorityRepository.findOneByName(authorityName);
                        if (oa.isPresent()) {
                            authority = oa.get();
                        }
                    }
                    if (authority != null) {
                        roleAuthority.setAuthority(authority);
                        roleAuthorityRepository.syncOne(roleAuthority);
                    }
                });
            }
        });
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        loadDefaultUserRoles(new ClassPathResource("default_user_roles.json"));
    }
}
