package adsbrecorder.user.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.RoleAuthority;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.repo.AuthorityRepository;
import adsbrecorder.user.repo.RoleAuthorityRepository;
import adsbrecorder.user.repo.RoleRepository;
import adsbrecorder.user.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    private RoleAuthorityRepository roleAuthorityRepository;
    private AuthorityRepository authorityRepository;
    private Map<String, Role> allRoles;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, RoleAuthorityRepository roleAuthorityRepository, AuthorityRepository authorityRepository) {
        this.roleRepository = requireNonNull(roleRepository);
        this.roleAuthorityRepository = requireNonNull(roleAuthorityRepository);
        this.authorityRepository = requireNonNull(authorityRepository);
    }

    @PostConstruct
    public void cacheAllRoles() {
        this.allRoles = new ConcurrentHashMap<String, Role>();
        roleRepository.findAll().forEach(role -> this.allRoles.put(role.getRoleName(), this.retrieveRoleAuthorities(role)));
    }

    @Override
    public Role findByRoleName(String roleName) {
        Optional<Role> role = roleRepository.findOneByRoleName(roleName);
        if (role.isPresent()) {
            return this.retrieveRoleAuthorities(role.get());
        }
        return Role.invalidRole();
    }

    @Override
    public Role findRoleById(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent()) {
            return this.retrieveRoleAuthorities(role.get());
        }
        return Role.invalidRole();
    }

    @Override
    public Role saveRole(Role role) {
        final Role role0 = roleRepository.save(role);
        Set<Authority> authoritiesSet = role.getAuthorities();
        if (authoritiesSet != null && authoritiesSet.isEmpty() == false) {
            authorityRepository.syncAll(authoritiesSet);
            authoritiesSet.forEach(auth -> {
                Optional<RoleAuthority> existRA = roleAuthorityRepository.findOneByRoleAndAuthority(role0, auth);
                if (existRA.isEmpty()) {
                    RoleAuthority ra = new RoleAuthority();
                    ra.setAuthority(auth);
                    ra.setRole(role0);
                    roleAuthorityRepository.save(ra);
                }
            });
        }
        return role0;
    }

    @Override
    public List<Role> findAvailableRoles(User user) {
        Set<String> userRoleNames = user.getRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toSet());
        return this.allRoles.keySet().stream()
                .flatMap(roleName -> userRoleNames.contains(roleName) ? Stream.empty() : Stream.of(this.allRoles.get(roleName)))
                .collect(Collectors.toList());
    }

    private Role retrieveRoleAuthorities(Role role) {
        List<RoleAuthority> authorities = roleAuthorityRepository.findAllByRole(role);
        Set<Authority> authoritiesSet = new HashSet<Authority>();
        authorities.forEach(ra -> authoritiesSet.add(ra.getAuthority()));
        role.setAuthorities(authoritiesSet);
        return role;
    }

    @Override
    public Role findOrSaveByRoleName(String roleName, String displayName, String description) {
        Optional<Role> role = roleRepository.findOneByRoleName(roleName);
        if (role.isPresent()) {
            Role role0 = role.get();
            role0.setDisplayName(displayName);
            role0.setDescription(description);
            return this.retrieveRoleAuthorities(this.roleRepository.save(role0));
        }
        Role newRole = new Role();
        newRole.setRoleName(roleName);
        newRole.setDisplayName(displayName);
        newRole.setDescription(description);
        return this.roleRepository.save(newRole);
    }
}
