package adsbrecorder.user.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import adsbrecorder.user.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findOneByRoleName(String roleName);
}
