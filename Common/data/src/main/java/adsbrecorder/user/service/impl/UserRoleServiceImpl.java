package adsbrecorder.user.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.RoleAuthority;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserRole;
import adsbrecorder.user.entity.UserRoleType;
import adsbrecorder.user.repo.RoleAuthorityRepository;
import adsbrecorder.user.repo.RoleRepository;
import adsbrecorder.user.repo.UserRoleRepository;
import adsbrecorder.user.service.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private UserRoleRepository userRoleRepository;
    private RoleAuthorityRepository roleAuthorityRepository;
    private RoleRepository roleRepository;
    private final transient Set<Role> defaultRoles;

    @Value("${adsbrecorder.userservice.default_roles:}")
    private String listOfDefaultRoles;

    @Value("${adsbrecorder.userservice.default_roles_separator:,}")
    private char separator;

    @Autowired
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, RoleRepository roleRepository, RoleAuthorityRepository roleAuthorityRepository) {
        this.userRoleRepository = requireNonNull(userRoleRepository);
        this.roleRepository = requireNonNull(roleRepository);
        this.roleAuthorityRepository = requireNonNull(roleAuthorityRepository);
        this.defaultRoles = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @PostConstruct
    public void loadDefaultRoles() {
        if (!StringUtils.isEmpty(this.listOfDefaultRoles)) {
            this.listOfDefaultRoles = this.listOfDefaultRoles.trim();
            final String[] roleNames = StringUtils.split(this.listOfDefaultRoles, this.separator);
            Arrays.stream(roleNames).forEach(roleName -> {
                Optional<Role> role = roleRepository.findOneByRoleName(roleName.trim());
                if (role.isPresent()) {
                    this.defaultRoles.add(role.get());
                }
            });
        }
    }

    @Override
    public User assignDefaultRolesToUser(User user) {
        final Set<Role> currentRoles = this.getRolesForUser(user);
        final List<UserRole> userRoles = new ArrayList<UserRole>();
        this.defaultRoles.forEach(role -> {
            if (!currentRoles.contains(role)) {
                UserRole ur = new UserRole();
                ur.setRole(role);
                ur.setUser(user);
                ur.setCreationDate(new Date());
                ur.setRoleType(UserRoleType.DEFAULT_USER_ROLE);
                userRoles.add(ur);
            }
        });
        if (userRoles.size() > 0) {
            user.setUserRoles(this.userRoleRepository
                    .saveAll(userRoles)
                    .stream()
                    .collect(Collectors.toSet()));
        }
        return user;
    }

    @Override
    public UserRole assignRoleToUser(Role role, User user) {
        Optional<UserRole> exist = this.userRoleRepository.findOneByUserAndRole(user, role);
        if (exist.isPresent())
            return exist.get();
        UserRole ur = new UserRole();
        ur.setRole(role);
        ur.setUser(user);
        ur.setCreationDate(new Date());
        ur.setRoleType(UserRoleType.ADMIN_ASSIGNED_ROLE);
        return this.userRoleRepository.save(ur);
    }

    @Override
    public Set<Role> getRolesForUser(User user) {
        return getUserRoles(user).stream().map(ur -> ur.getRole()).collect(Collectors.toSet());
    }

    @Override
    public Set<UserRole> getUserRoles(User user) {
        List<UserRole> userRoles = userRoleRepository.findAllByUser(user);
        return userRoles.stream().map(ur -> {
            Role role = ur.getRole();
            List<RoleAuthority> ras = roleAuthorityRepository.findAllByRole(role);
            Set<Authority> authorities = new HashSet<Authority>();
            ras.forEach(ra -> authorities.add(ra.getAuthority()));
            role.setAuthorities(authorities);
            return ur;
        }).collect(Collectors.toSet());
    }

    @Override
    public void removeRoleFromUser(Role role, User user) {
        Optional<UserRole> uro =  userRoleRepository.findOneByUserAndRole(user, role);
        if (uro.isPresent()) {
            UserRole ur = uro.get();
            if (UserRoleType.ADMIN_ASSIGNED_ROLE.equals(ur.getRoleType())) {
                userRoleRepository.delete(ur);
            }
        }
    }
}
