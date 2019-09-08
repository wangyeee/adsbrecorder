package adsbrecorder.user.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import adsbrecorder.user.entity.Authority;
import adsbrecorder.user.entity.User;
import adsbrecorder.user.entity.UserAuthority;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {

    List<UserAuthority> findAllByUserAndAuthority(User user, Authority authority);
}
