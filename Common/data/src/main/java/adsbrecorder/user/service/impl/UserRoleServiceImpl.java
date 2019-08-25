package adsbrecorder.user.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserRole;
import adsbrecorder.user.repo.UserRoleRepository;
import adsbrecorder.user.service.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = requireNonNull(userRoleRepository);
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
        return this.userRoleRepository.save(ur);
    }
}
