package adsbrecorder.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.Role;
import adsbrecorder.user.entity.RoleAuthority;

public interface RoleAuthorityRepository extends JpaRepository<RoleAuthority, Long> {

    List<RoleAuthority> findAllByRole(Role role);
    Optional<RoleAuthority> findOneByRoleAndAuthority(Role role, Authority authority);

    default RoleAuthority syncOne(RoleAuthority roleAuthority) {
        Optional<RoleAuthority> ora = findOneByRoleAndAuthority(roleAuthority.getRole(), roleAuthority.getAuthority());
        if (ora.isPresent()) {
            return ora.get();
        }
        return save(roleAuthority);
    }
}
