package adsbrecorder.user.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import adsbrecorder.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByUsername(String username);
}
