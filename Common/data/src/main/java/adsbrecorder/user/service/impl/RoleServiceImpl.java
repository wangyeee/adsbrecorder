package adsbrecorder.user.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.RoleAuthority;
import adsbrecorder.user.repo.AuthorityRepository;
import adsbrecorder.user.repo.RoleAuthorityRepository;
import adsbrecorder.user.repo.RoleRepository;
import adsbrecorder.user.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    private RoleAuthorityRepository roleAuthorityRepository;
    private AuthorityRepository authorityRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, RoleAuthorityRepository roleAuthorityRepository, AuthorityRepository authorityRepository) {
        this.roleRepository = requireNonNull(roleRepository);
        this.roleAuthorityRepository = requireNonNull(roleAuthorityRepository);
        this.authorityRepository = requireNonNull(authorityRepository);
    }

    @Override
    public Role findByRoleName(String roleName) {
        Optional<Role> role = roleRepository.findOneByRoleName(roleName);
        if (role.isPresent()) {
            return retrieveRoleAuthorities(role.get());
        }
        return Role.invalidRole();
    }

    @Override
    public Role findRoleById(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent()) {
            return retrieveRoleAuthorities(role.get());
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

    private Role retrieveRoleAuthorities(Role role) {
        List<RoleAuthority> authorities = roleAuthorityRepository.findAllByRole(role);
        Set<Authority> authoritiesSet = new HashSet<Authority>();
        authorities.forEach(ra -> authoritiesSet.add(ra.getAuthority()));
        role.setAuthorities(authoritiesSet);
        return role;
    }
}
